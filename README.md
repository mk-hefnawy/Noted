# Noted
## Table of contents
* [Demo](#demo)
* [General info](#general-info)
* [Architecture](#architecture)
* [Technologies](#technologies)

## Demo
Working on it.

## General Info
Noted is an android application for taking notes. The user can create, edit, review, and delete notes. They also can specify a category for each note.

## Architecture
### Clean Architecture
![CLean-Architecture](https://user-images.githubusercontent.com/69528783/163322346-c353fd9b-3a45-4853-9b83-b44533629225.png)

* Data Layer
Response Objects, Repository Implementation, and Data sources live here. The actual DAO code goes here, whether it's remote or local.

* Business Layer
Domain Entities, Use cases, and The Repository Abstraction (Interface) live here. 
All the things that the user can do in the application are mapped to use cases programatically.

* Presentation Layer
Request Objects, Activities, Fragments, and ViewModels live here. Activities and Fragments take the user events then trigger a specific methods of the ViewModel to handle that event. 
The viewModel triggers the use case for that event and subscribe to the use case's Observables.

### Clean Architecture with Reactive Programming
![Clean-with-RX](https://user-images.githubusercontent.com/69528783/163322998-44951956-6c5d-4d33-a94e-054e3590a847.png)


## Technologies
* Kotlin
* ViewModel
* RxAndroid
* Room Database
* Hilt
* Gson



