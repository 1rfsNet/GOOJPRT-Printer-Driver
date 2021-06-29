# GOOJPRT-Printer-Driver
Printer Driver and Apps for GOOJPRT Thermal Printer - Version 6.33 from 2019.

This is a copy of the driver CD included with the following GOOJPRT printers:

- (JICAI / OCOM) POS-Q1/Q2
- JP-58H-U
- JP-58H-WIFI
- MTP-2 / MTP-II 58mm Bluetooth Printer
- MTP-3 80mm Bluetooth Printer
- POS58
- POS80
- POS80W
- POS120

If the printer has Bluetooth but doesn't support BT printing with Windows, try the following:
- Pair the printer with Windows (for me it is visible as "MTP-2" with error "Driver is unavailable")
- Install the driver and select Serial Port (COM Port) during installation
- Open the Control Panel (the old one, not the new Settings menu)
- Enter "Bluetooth" in search bar and select "Change Bluetooth Settings"
- In "COM Ports" tab you can see the COM ports being used by the printer
- Now select these ports in the printer installation and repeat installation for the second port (not sure if necessary)
- You should now be able to print to a new printer ("POS58 Printer" in my case)
- After first print it will ask you to setup the printer ("MTP-2" for me). Click on the message and follow the steps. If you have to enter a pin use 0000 or something else. If this doesn't work, try to pair the printer again. 
