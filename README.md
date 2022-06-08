# Once

A library to make sure code blocks are executed only once

## Getting Started

Configuring the dependencies:

```kotlin
dependencies {
    implementation("io.johnsonlee:once:1.1.0")
}
```

Then, you can use it in your code:

```kotlin
class Gretting {

  val once = Once<Unit>()
  
  fun hello(name: String): Unit = once {
    println("Hello, ${name}")
  }

}
```
