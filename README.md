ImageRenameByExif
=========

ImageRenameByExif is a library to automatically rename and order image based on the EXIF data ([what is it?](http://it.wikipedia.org/wiki/Exchangeable_image_file_format))

Use
---
  - You can include the sources or the compiled JAR to a project and use it, or...
  - You can use the demo application provided compiling from Eclipse or launching the jar from a console with the syntax
```sh
java -jar ImageRenameByExifGui.jar
```
and selecting a source and a destination folder

How does it work
---
The library will cycle all the files in the provided source directory and copy all the Jpeg files to a provided destination directory naming it with the shooting date reported in the EXIF data or, if unavailable, the file creation date.

Version
----
1.0

Thanks
-----------

Thanks to drewnoakes for the [EXIF Metadata Extractor library](https://drewnoakes.com/code/exif/)

Thanks Adobe for the [XMP library](http://www.adobe.com/devnet/xmp.html)


License
----

ImageRenameByExif and the demo project are released under GPL v3 License

The EXIF Metadata Extractor library is released under the Apache 2 Licence and cannot be considered part of this project, it is an indipendent library with an indipendent licence. The sources are not included in this project, the library is included in binary form.

The XMP library is released under the BSD License and cannot be considered part of this project, it is an indipendent library with an indipendent licence. The sources are not included in this project, the library is included in binary form.

