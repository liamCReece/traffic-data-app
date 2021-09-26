
#### JDK 11 is required
<br><br/>
#### Assumptions
For output periods with least cars, only periods with FULL 3 data points will be considered. If 
there are multiple time periods with equal lowest car count. All of them will be in output.
<br><br/>
#### Command build
```
./gradlew clean build
```
this will generate the jar executable file ```traffic-data-app-0.0.1-SNAPSHOT.jar``` in the source directory.
<br><br/>
#### Command to run the app
```
java -jar traffic-data-app-0.0.1-SNAPSHOT.jar 1_exampleInput.txt
```
inputFile needs to be in the same directory with the jar file. User must have write permission in the directory to 
allow export of the output file.

<br><br/>
#### output files
```1_total_vehicles.txt```
<br><br/>
```2_output_top3.txt```
<br><br/>
```3_output_daily_status.txt```
<br><br/>
```4_output_periods_with_least_traffic.txt```