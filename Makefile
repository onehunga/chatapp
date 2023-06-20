build:
	gradle installDist

clean:
	rm local

run_db:
	surreal start -u root -p root file:./surreal