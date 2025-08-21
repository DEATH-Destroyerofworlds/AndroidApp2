#!/usr/bin/env python3
"""
Build script for creating executable
"""
import os
import subprocess
import sys


def build_exe():
    """Build the executable using PyInstaller"""
    
    # PyInstaller command with all necessary options
    cmd = [
        "pyinstaller",
        "--onefile",                    # Create a single executable file
        "--windowed",                   # Don't show console window (for GUI apps)
        "--name=ProductScraper",        # Name of the executable
        "--clean",                      # Clean PyInstaller cache
        "--noconfirm",                  # Replace output directory without asking
        "--add-data=scraping.py;.",     # Include scraping module
        "--add-data=gui.py;.",          # Include GUI module
        "--hidden-import=selenium",     # Ensure selenium is included
        "--hidden-import=undetected_chromedriver",  # Ensure UC is included
        "--hidden-import=websocket",    # Include websocket modules
        "--hidden-import=tkinter",      # Include tkinter
        "main.py"                       # Main script
    ]
    
    print("Building executable...")
    print(f"Command: {' '.join(cmd)}")
    
    try:
        result = subprocess.run(cmd, check=True, capture_output=True, text=True)
        print("Build successful!")
        print("Executable created in: dist/ProductScraper.exe")
        return True
    except subprocess.CalledProcessError as e:
        print(f"Build failed: {e}")
        print(f"Error output: {e.stderr}")
        return False


if __name__ == "__main__":
    build_exe()