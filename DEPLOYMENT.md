# CI/CD Deployment Guide

This guide sets up automated deployment of your Spring Boot application to DigitalOcean using GitHub Actions.

## Architecture

```
Push to GitHub main branch
         ↓
GitHub Actions triggered
         ↓
Build Spring Boot JAR (on self-hosted runner)
         ↓
Deploy JAR to Droplet
         ↓
Restart systemd service
         ↓
✓ Application running
```

## Prerequisites

- DigitalOcean Droplet with Ubuntu (IP: 159.89.82.236)
- SSH access to Droplet
- GitHub repository access
- Your Spring Boot project has a `pom.xml` (Maven)

## Step-by-Step Setup

### Step 1: SSH into your Droplet

```bash
ssh root@159.89.82.236
```

Or if using SSH key:
```bash
ssh -i /path/to/your/key ubuntu@159.89.82.236
```

### Step 2: Run the setup script

Download and run the setup script:

```bash
curl -O https://raw.githubusercontent.com/ttalphine-git/qhxprogit/main/setup-droplet.sh
bash setup-droplet.sh
```

Or run the commands from `setup-droplet.sh` manually if you prefer.

This will:
- Install Java 21 and Maven
- Create `/opt/springboot-app` directory
- Set up systemd service for your Spring Boot app
- Download GitHub Actions runner

### Step 3: Register the GitHub Actions Runner

After the setup script completes, you'll see instructions. Follow these steps:

1. Go to your GitHub repo: https://github.com/ttalphine-git/qhxprogit
2. Navigate to: **Settings → Actions → Runners → New self-hosted runner**
3. Select **Linux** and **x64**
4. Copy the configuration command from GitHub (it will have a unique token)
5. On your Droplet, in the `~/actions-runner` directory, run that command:

```bash
cd ~/actions-runner
./config.sh --url https://github.com/ttalphine-git/qhxprogit --token YOUR_GITHUB_TOKEN_HERE
```

6. Start the runner in the background:

```bash
./run.sh &
```

Or install it as a service for auto-start:

```bash
sudo ./svc.sh install
sudo systemctl start actions.runner.ttalphine-git-qhxprogit.*
```

### Step 4: Verify the Runner is Connected

In GitHub, go to **Settings → Actions → Runners** and you should see your Droplet listed as **Idle**.

### Step 5: Test the Deployment

Push a change to your main branch:

```bash
git push origin main
```

GitHub Actions will automatically:
1. Build your Spring Boot application
2. Copy the JAR to `/opt/springboot-app/app.jar`
3. Restart the systemd service
4. Your app will be running on the Droplet

### Step 6: Access your Application

Once deployed, your Spring Boot app will be running on the Droplet. By default:
- **Internal access:** `http://localhost:8080` (from the Droplet)
- **External access:** `http://159.89.82.236:8080` (from your machine)

Adjust the port in your `application.properties` or `application.yml` if needed:

```properties
server.port=8080
```

## Troubleshooting

### Runner is not connecting
Check runner logs:
```bash
cd ~/actions-runner
tail -f runner-startup.log
```

### Application fails to start
Check systemd logs:
```bash
sudo journalctl -u springboot-app -n 50
```

### Java not found
Verify Java is installed:
```bash
java -version
```

### Permission denied errors
The runner needs sudo access. Add your user to sudoers:
```bash
sudo visudo
# Add this line at the end:
# ubuntu ALL=(ALL) NOPASSWD: /bin/systemctl
```

## File Structure

```
qhxprogit/
├── .github/workflows/
│   └── deploy.yml           # GitHub Actions workflow
├── src/
├── pom.xml                  # Maven configuration
├── DEPLOYMENT.md            # This file
└── setup-droplet.sh         # Droplet setup script
```

## What Happens on Each Push

1. You push code to `main` branch
2. GitHub Actions automatically:
   - Checks out your code
   - Builds the JAR with Maven
   - Copies JAR to Droplet
   - Stops the old application
   - Starts the new application with systemd

You can monitor the deployment in GitHub → Actions tab.

## Security Notes

- The systemd service runs as your user, not root
- GitHub Actions runner communicates securely with GitHub
- Secrets and sensitive data should be managed via GitHub Secrets
- Keep your Droplet's SSH key secure

## Next Steps

1. Configure your `pom.xml` to ensure the JAR is built with a main class:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.example.YourMainClass</mainClass>
    </configuration>
</plugin>
```

2. Add environment variables if needed in `.github/workflows/deploy.yml`
3. Monitor logs after each deployment

## Support

For issues with GitHub Actions, check the Actions tab in your repository.
For issues with the Droplet, SSH in and check systemd logs.
