FilmClub
===

Simple android app to browse [The Movie Database](https://www.themoviedb.org/) dataset.

## Features

Currently you can

 - Browse movies
 - Sort by popularity or rating
 - View simple details

# Screenshots

![Portrait](/screenshots/portrait1.png?raw=true)
![Landscape](/screenshots/landscape1.png?raw=true)

# Installation

Simply download and run will not work in this case, as you have to have a [The Movie Database](https://www.themoviedb.org/documentation/api) API key.

Once obtained you'll need to create a ~/.apiKeys key-value file where you need to add it for key *movieDbApiKey*

    movieDbApiKey = abcdefghijklmnopqrstuvwxyz1234567890

or you simply change the retrieval procedure in `app/build.gradle`

## Libraries

- [Butterknife](https://github.com/JakeWharton/butterknife)
- [Glide](https://github.com/bumptech/glide)
- [Retrofit](https://github.com/square/retrofit)

# License

Source code of this project is available under the standard MIT license. Please see [the license file](LICENSE.md).
