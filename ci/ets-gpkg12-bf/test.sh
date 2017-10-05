#!/bin/sh

key='4bc81304-77f5-4ded-91f3-d2d087fffcfe'

echo $BF_GPKG_KEY

rootPath=$(pwd)
chmod a+r src/main/config/test-run-props.xml

rm -rf target/testng
rm -f test.gpkg
rm src/main/config/test-run-props.xml

echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n
<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n
<properties version=\"1.0\">\n
<comment>Test run arguments</comment>\n
<entry key=\"iut\">file://$rootPath/test.gpkg</entry>\n
<entry key=\"ics\">Core,Tiles,Features,Extensions,RTree Index,Metadata,Schema,CRS WKT,Tiled Gridded Elevation Data</entry>\n
</properties>" >> src/main/config/test-run-props.xml



curl --user $BF_GPKG_KEY: -o test.gpkg -X GET \
  https://bf-api.int.geointservices.io/v0/job/2ec7c319-19bb-48d7-b48a-ecd59ce12664.gpkg
  
chmod a+r test.gpkg


echo "CHECK DIR"
ls -l
pwd

echo "CHECK USER"
id -un
id -Gn

sleep 2s

java -jar target/ets-gpkg12-0.2-SNAPSHOT-aio.jar --outputDir target src/main/config/test-run-props.xml

sleep 2s

filePath=$( ls target/testng)

sleep 2s

chmod 755 target/testng/$filePath/testng-results.xml

fail=$(awk '/passed=/ { print $3}' target/testng/$filePath/testng-results.xml)

pass=$(awk '/passed=/ { print $5}' target/testng/$filePath/testng-results.xml | tr -d '>')

failComp='failed="0"'

passComp='passed="0"'

rootPath= pwd

echo "TEST RESULTS"
cat target/testng/$filePath/testng-results.xml

if [ "$pass" != "$passComp" ]  &&  [ "$fail" == "$failComp" ]; then
echo "GeoPackage Passed Validation" 
else
echo "GeoPackage Failed Validation"
exit 42;
fi
sleep 2s


