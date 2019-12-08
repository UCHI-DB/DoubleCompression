while IFS= read -r line; do
	first="$(cut -d' ' -f1 <<<$line)"
	second="$(cut -d' ' -f2 <<<$line)"
	if [ "$first" = "argonne" ]; then
		path="$(pwd)/data/$first/$first/$second"
	else
		path="$(pwd)/data/$first/$(cut -d'_' -f1 <<<$second)/$second"
	fi
	rawSize="$(stat -f "%z" "$path")"
	gzip -9 "$path"
	compressedSize="$(stat -f "%z" "$path.gz")"
	gunzip "$path.gz"
	echo "$rawSize,$compressedSize"
done < fileList.csv