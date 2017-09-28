key='4bc81304-77f5-4ded-91f3-d2d087fffcfe'

echo $BF_GPKG_KEY

curl --user $BF_GPKG_KEY: -o test.gpkg -X GET \
  https://bf-api.int.geointservices.io/v0/job/2ec7c319-19bb-48d7-b48a-ecd59ce12664.gpkg

ls -l
pwd

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

echo "TEST RESULTS"
cat target/testng/$filePath/testng-results.xml

if [ "$pass" != "$passComp" ]  &&  [ "$fail" == "$failComp" ]; then
echo "GeoPackage Passed Validation" 
else
echo "GeoPackage Failed Validation"
exit 42;
fi
sleep 2s

rm -rf target/testng
rm -f test.gpkg
