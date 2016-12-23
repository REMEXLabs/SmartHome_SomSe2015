gcal-fix-openHAB
================

updated gcal plugin for openhab (temporary solution)

This fix was developed by Avi Wolf and originaly documented in a [Google groups thread](https://groups.google.com/forum/#!msg/openhab/LbqKLEdlDCk/ltz-a4Wi73MJ)

In mid November 2014 Google stoped using their V2 API. More details can be found
[here](https://developers.google.com/google-apps/calendar/v2/developers_guide_protocol). 
This was a braking change for the openHAB gcal plugin. This repo is an atempt at
creating consise instructions for fixing this.

### Steps to perform on the google side:
1. Go to the [Google Developers Console](https://console.developers.google.com/) and login with any google user (you can use any google account but I’m using here the Calendar user as well).
1. Create a new project. You can call it openhab.
1. Under the project that you have just created, in the sidebar on the left, expand APIs & auth. Next, click APIs. In the list of APIs, make sure the status is ON for the Calendar API.
1. In the sidebar on the left, select Consent screen, fill PRODUCT NAME with the value openhab_jcal and select Email address. Save the form (PRODUCT NAME is not required to be openhab_jcal but this will be the name on the consent page).
1. Once the form is saved, you will get a dialog to create a Client ID. If you didn’t get the dialog automatically (browser dependent), In the sidebar on the left, select Credentials and on the right press Create new Client ID
1. Application type should be Installed application and Installed application type should be Other.
1. Press Create Client ID
1. Once the Client ID was created, press Download JSON and save the file on your machine.

### Steps to perform with the command line(TBD):
Call the getAuthURL utiltiy in the gcalHealper script found in this repo, and provide the location of the json file you downloaded from the Google Developers Console. (Replace `./gcalHealper.sh` with `gcalHealper.bat` on Windows systems)

###### Unix
```bash
$ ./gcalHealper.sh getAuthURL <path_to_json_file>
```
###### Windows
```bash
gcalHealper.bat getAuthURL <path_to_json_file>
```
You will see the Authorization URL printed to the console:
```bash
Gcal healper Version 1.0



Please open the following URL in a browser and complete the authorization flow:

<URL_to_open>
```
Copy the URL to your favorite browser. You will be asked to login with the Calendar google user.

From there you will be redirected to a consent page with the application name you defined previously in the Google Developers Console asking for permission to access your Calendar.

Press Accept and you will be redirected to a page containing a code. Copy this code and go back to the console.

Call getUserCredentials command and provide the path to the *.json file, the code and the location where you want the client credentials to be saved.
```bash
$ ./gcalHealper.sh getUserCredentials <path_to_json_file> <copied_code> <path_to_save_credentials_in>
```
If all goes well and you receive no error, you will see output simmilar to the following. Note that the path you specified for the new credentials file has been ignored. Its placed in a directory specified in the output.
```bash
Gcal healper Version 1.0


Credentials stored sucessfully at: /home/<user_name>/.store/gcal


Validating Credentials...

Query Calendars list....
Jan 01, 2015 10:35:41 AM com.google.api.client.googleapis.services.AbstractGoogleClient <init>
WARNING: Application name is not set. Call Builder#setApplicationName.
---------------- Calendars List ---------------

-----------------------------------------------
ID: <clendar_id>@group.calendar.google.com
Summary: Irrigation
Description: water the yard!

-----------------------------------------------
ID: <google_user>@gmail.com
Summary: <google_user>@gmail.com

-----------------------------------------------
ID: #contacts@group.v.calendar.google.com
Summary: Birthdays
Description: Displays birthdays of people in your Google+ circles and Google Contacts. Also displays anniversary and other event dates from Google Contacts, if applicable.

-----------------------------------------------
ID: en.usa#holiday@group.v.calendar.google.com
Summary: Holidays in United States
Description: Holidays and Observances in United States
-----------------------------------------------
```

## Updating the openHAB Directory Tree
1. Remove the old `org.openhab.io.gcal_1.5.1.jar` from the `openhab/server/plugins` directory if you have one there.
1. Put the `org.openhab.io.gcal_1.5.1.jar` from this repo into the `openhab/addons` directory.

## Updating openhab.cfg
The `gcal:username`, `gcal:password` and `gcal:url` are nolonger used so comment them out.
Next, add the following configurations:
```
gcal:calendarid=<leave_blank_for_primary_calendar>
gcal:secretsfilepath=<json_file_path>
gcal:credentialsdatastorefolder=<path_to_folder_containing_credentials_file>
```
These configurations might look like this on a unix system:
```
gcal:calendarid=
gcal:secretsfilepath=/opt/openhab1.5.1/client_secrets.json
gcal:credentialsdatastorefolder=/opt/openhab1.5.1/gcal_store
```

## Add gcal logging
Add the following line to `openhab/configurations/logback.xml`. You will see similar lines near the end of the file. 
```xml
    <logger name="org.openhab.io.gcal" level="TRACE" />
```
