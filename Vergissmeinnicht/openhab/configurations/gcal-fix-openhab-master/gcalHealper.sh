#!/bin/sh

java -cp lib/gcalHealper.jar:lib/google-api-client-1.19.0.jar:lib/google-api-services-calendar-v3-rev103-1.19.0.jar:lib/google-http-client-1.19.0.jar:lib/google-http-client-jackson2-1.19.0.jar:lib/google-oauth-client-1.19.0.jar:lib/jackson-core-2.1.3.jar org.openhab.io.gcal.authorization.Cli $1 $2 $3
