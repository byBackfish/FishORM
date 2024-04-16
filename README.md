# Simple Java SQL ORM

## Should I use this? 
> Short answer: No. 
> Long answer: It depends. It works fine for me and I prefer it over other solutions, but it might not for you. Its also not optimized, so you might run into issues regarding memory leaks or performance in general.

## Examples:
See [Examples](https://github.com/byBackfish/FishORM/src/main/java/example/)

## Installation:

### Gradle:
```gradle
// Add jitpack as the repository:
  repositories {
			maven { url 'https://jitpack.io' }
  }

// Add FishORM as a dependency
  dependencies {
      implementation 'com.github.byBackfish:FishORM:-SNAPSHOT'
	}
```

### Maven:

```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
	    <groupId>com.github.byBackfish</groupId>
	    <artifactId>FishORM</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```


## FAQ:
### Why not Kotlin?
> Simple. This started as a small utility library for a project in university, and I was not allowed to use Kotlin. If I could, I would totally rewrite this in Kotlin. Maybe in the future?

### Why not Java Records?
> All values in a record are immutable, which completely ruins my `#update()` functionality. They also don't support anything other than implementing from an interface, which disallows static methods. This would ruin the `Model#all()` (and all other static helper methods) functionality.
