build:
	gradle installDist

.PHONY: client
client:
	sh ./client/build/install/client/bin/client

.PHONY: server
server:
	sh ./server/build/install/server/bin/server

clean:
	rm local

run_db:
	surreal start -u root -p root file:./surreal