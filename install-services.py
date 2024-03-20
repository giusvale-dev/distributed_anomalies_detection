#!/usr/bin/python3.10
# Installation script
#@Author Giuseppe Valente <valentepeppe@gmail.com>
####

import os
import subprocess

projects = ['authentication-service', 'database-service', 'userservice']

for project in projects:
    
    os.chdir(project)
    
    # Run mvn command
    try:
        subprocess.run(["mvn", "dependency:go-offline", "-B", "-DskipTests=true"], check=True)
        subprocess.run(["mvn", "package", "-DskipTests=true"], check=True)
        print(f"Successfully ran mvn command in {project}")
    except subprocess.CalledProcessError as e:
        print(f"Failed to run mvn command in {project}: {e}")

    # Change back to the original directory
    os.chdir("..")


