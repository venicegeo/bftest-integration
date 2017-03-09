
failing_tests=""
passing_tests=""

RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;36m'
NC='\033[0m'

for i in *TEST.sh
do
	echo "Running $i"
	"./$i" && passing_tests+="\n$i" || failing_tests+="\n$i"
done

if [ ! -z "$failing_tests" ]; then
	echo -e "$RED~~~~~~~ FAILING ~~~~~~~"
	printf "$failing_tests\n\n"
	echo -e "~~~~~~~~~~~~~~~~~~~~~~~$NC\n"
fi

if [ ! -z "$passing_tests" ]; then
	echo -e "$GREEN~~~~~~~ PASSING ~~~~~~~"
	printf "$passing_tests\n\n"
	echo -e "~~~~~~~~~~~~~~~~~~~~~~~$NC\n"
fi
