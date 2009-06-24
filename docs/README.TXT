The Echo Nest Java API
----------------------

This is a Java client for the Echo Nest Java API.  To use the API 
get a developer key from developer.echonest.com.

    http://developer.echonest.com/


License:

    New BSD License - http://www.opensource.org/licenses/bsd-license.php



Quick Start
-------------

Here's some sample code that shows how to get similar artists
with the API:

  // Create the EchoNest class
  echoNest = new EchoNest("Your API Key");

  // Search for weezer
  List<Artist> artists = echoNest.searchArtist("weezer", false);

  // Find similar artists

  if (artists.length() > 0) {
      List<Scored<Artist>> artists = echoNest.getSimilarArtists(artist, 0, 5);
      for (Scored<Artist> sartist : artists) {
          System.out.println(sartist.getItem().getName());
      }
  }


See the 'examples' package for more code samples

Revision History
----------------

Version 1.1  - June 24, 2009
    - fixed bug where cache would grow without limits
    - added 'setMaxCacheSize' method to artist and track APIs
    - added 'setMiniumumCommandPeriod' method to artist and track APIs
    - removed extraneous test code

Version 1.0  - April 7, 2009
    - First release
    - Some minor refactorings to make javadocs better
    - javadoc improvements

Version .2  - April 6, 2009
    - added support for the track-level methods
    - improved the documentation
    - added some examples
    
Version .1  - March 27, 2009
    Initial Release
