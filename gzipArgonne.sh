counter=1
while [ $counter -le 1059 ]
do
	gunzip "gzipArgonne/argonne/$counter.gz"
	((counter++))
done