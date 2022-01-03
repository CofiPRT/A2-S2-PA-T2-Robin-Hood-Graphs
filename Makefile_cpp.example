# Exemplu de Makefile pentru tema facuta in C++
#
# tag-uri obligatorii (nume + comportament identic)
# build    => compileaza toata tema 
#             (ATENTIE! E important - NU compilati in tag-urile de run.)
# run-p$ID => ruleaza problema cu ID-ul specificat (1, 2, 3, 4)
# clean    => sterge toate fisierele generate

# Acest Makefile presupune ca aveti toate sursele intr-un director src/

build: p1 p2 p3 p4

p1: src/p1.cpp
	g++ -std=c++17 -Wall -Wextra src/p1.cpp -o p1

p2: src/p2.cpp
	g++ -std=c++17 -Wall -Wextra src/p2.cpp -o p2

p3: src/p3.cpp
	g++ -std=c++17 -Wall -Wextra src/p3.cpp -o p3

p4: src/bonus.cpp
	g++ -std=c++17 -Wall -Wextra src/bonus.cpp -o p4

run-p1:
	./p1
run-p2:
	./p2
run-p3:
	./p3
run-p4:
	./p4

clean:
	rm -f p1 p2 p3 p4
