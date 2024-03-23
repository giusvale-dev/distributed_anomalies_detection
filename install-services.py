#################################################
# Installation script
#@Author Giuseppe Valente <valentepeppe@gmail.com>
##################################################

import os
import subprocess

def main():
    projects = ['authentication-service', 'database-service', 'userservice']
    
    for project in projects:
        
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

if __name__ == "__main__":
    main()
