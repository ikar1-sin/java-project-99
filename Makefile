run-dist:
	build/insall/app/bin/app
clean:
	gradle clean
build:
	gradle clean build
test:
	./gradlew test
report:
	./gradlew jacocoTestReport
lint:
	./gradlew checkstyleMain
run:
	./gradlew bootRun

.PHONY: build

