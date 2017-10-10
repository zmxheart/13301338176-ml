# coding: utf-8

import time

from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice

APK = "example.apk"
PACKAGE = "com.example.package"
ACTIVITY = "com.example.package.activity"

def log(fn, device):
    msg = device.shell('logcat -d')
    f_log = open(fn, 'at')
    if msg is None:
        msg = 'None'
    f_log.write(msg.encode('utf-8'))
    f_log.close()    
    device.shell('logcat -c')

if __name__ == '__main__':
    device = MonkeyRunner.waitForConnection()
    device.removePackage(PACKAGE) # Uninstall package if already installed
    device.shell('logcat -c') # Clear log buffer
    device.installPackage(APK) # Install the application
    log('install.log', device) # Write install logs
    run_component = PACKAGE + '/' + ACTIVITY
    device.startActivity(component=run_component) # Launch the application
    time.sleep(10) # Wait 10 seconds
    log('start.log', device) # Write launch logs
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP) # Open a menu
    screen = device.takeSnapshot() # Capture a screenshot
    screen.writeToFile('screenshot.png', 'png') # Save to screenshot.png
    log('run.log', device) # Write test logs
    device.removePackage(PACKAGE) # Uninstall the application
    log('uninstall.log', device) # Write uninstall logs
