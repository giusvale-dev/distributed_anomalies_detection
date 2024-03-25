#!/usr/bin/env python3
#################################################
# Installation script
#@Author Giuseppe Valente <valentepeppe@gmail.com>
##################################################

import os
import subprocess

java_projects = ['authentication-service', 'database-service', 'userservice']
angular_projects = ['user-manager-ui']

for project in java_projects:
    os.chdir(project)
    # Run mvn command
    try:
        subprocess.run(["mvn", "clean"], check=True)
        subprocess.run(["mvn", "dependency:go-offline", "-B", "-DskipTests=true"], check=True)
        subprocess.run(["mvn", "package", "-DskipTests=true"], check=True)
        print(f"Successfully ran mvn command in {project}")
    except subprocess.CalledProcessError as e:
        print(f"Failed to run mvn command in {project}: {e}")
    
    # Change back to the original directory
    os.chdir("..")

for project in angular_projects:
    os.chdir(project)
    # Build projects
    try:
        subprocess.run(["rm", "-rf", "dist"], check=True)
        subprocess.run(["ng", "build"], check=True)
        print(f"Successfully ran mvn command in {project}")
    except subprocess.CalledProcessError as e:
        print(f"Failed to run mvn command in {project}: {e}")
    os.chdir("..")