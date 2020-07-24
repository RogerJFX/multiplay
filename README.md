Multiplay
-

Just some awful hack to get my wife's issues done.

No tests and no clue if everything works in the end.

I'm just winning against my wife, so everything is ok so far.

Running at http://crazything.de

&#x1f92a;

animate() {
	c=0
	x=0
	printf "|"
	while [ "$x" -ne 4 ]; do
		if [ "$c" -eq 0 -o "$c" -eq 4 ]
		then
			printf "\b|"
		elif [ "$c" -eq 1 -o "$c" -eq 5 ]
		then
			printf "\b/"
		elif [ "$c" -eq 2 -o "$c" -eq 6 ]
		then
			printf "\b-"
		elif [ "$c" -eq 3 -o "$c" -eq 7 ]
		then
			printf "\b\\"
		fi
		if [ "$c" -eq 7 ] 
		then
			c=-1
			x=$((x+1))
		fi 
		c=$((c+1))
		sleep 0.2
	done
	printf "\b"
}
