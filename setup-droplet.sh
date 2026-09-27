#!/bin/bash
set -e

echo "=== Spring Boot CI/CD Setup Script ==="
echo "Installing dependencies and setting up GitHub Actions runner..."

# Update system
sudo apt-get update
sudo apt-get upgrade -y

# Install Java 21 (required for Spring Boot)
sudo apt-get install -y openjdk-21-jdk

# Install Maven
sudo apt-get install -y maven

# Verify installations
echo "Java version:"
java -version
echo "Maven version:"
mvn -version

# Create app user (if not exists)
id appuser > /dev/null 2>&1 || sudo useradd -m -s /bin/bash appuser

# Create app directory
sudo mkdir -p /opt/springboot-app
sudo chown appuser:appuser /opt/springboot-app
sudo chmod 755 /opt/springboot-app

# Create systemd service file
sudo tee /etc/systemd/system/springboot-app.service > /dev/null <<EOF
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
Type=simple
User=appuser
WorkingDirectory=/opt/springboot-app
ExecStart=/usr/bin/java -jar /opt/springboot-app/app.jar
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
EOF

# Reload systemd
sudo systemctl daemon-reload
echo "✓ Systemd service created"

# Install GitHub Actions Runner
echo ""
echo "=== Installing GitHub Actions Runner ==="

# Create runner directory
mkdir -p ~/actions-runner
cd ~/actions-runner

# Download the latest runner
curl -o actions-runner-linux-x64-2.320.0.tar.gz -L https://github.com/actions/runner/releases/download/v2.320.0/actions-runner-linux-x64-2.320.0.tar.gz

# Extract
tar xzf ./actions-runner-linux-x64-2.320.0.tar.gz

echo ""
echo "✓ GitHub Actions runner installed"
echo ""
echo "=== NEXT STEPS ==="
echo ""
echo "1. Go to your GitHub repo: https://github.com/ttalphine-git/qhxprogit"
echo "2. Navigate to: Settings → Actions → Runners → New self-hosted runner"
echo "3. Copy the 'Configure' command and run it in ~/actions-runner on your Droplet"
echo ""
echo "Example (you need to replace TOKEN with actual token from GitHub):"
echo "./config.sh --url https://github.com/ttalphine-git/qhxprogit --token YOUR_TOKEN"
echo ""
echo "4. After configuration, start the runner:"
echo "./run.sh &"
echo ""
echo "Setup complete!"
