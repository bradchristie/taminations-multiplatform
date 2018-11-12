
//  This version number is reported to the client.  If it does not match
//  the version number saved when files were downloaded, the client
//  will request a new download to fill the new cache.
self.version = "1.5.14";
//  This is run just once, when the user loads Taminations
self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(self.version).then(function(cache) {
      return cache.addAll([
         //  These are the files needed to start up Taminations
         //  or are otherwise not loaded in the cache by the code below
        "index.html",
        "lib/kotlin.js",
        "taminations.js",
        "favicon.ico",
        "tam120.png",
        "notsupported.html",
        "assets/src/callindex.xml",
        "assets/src/calls.xml",
        "assets/src/framecode.js",
        "assets/src/formations.xml",
        "assets/src/moves.xml",
        "assets/src/tamination.css",
        "assets/info/about.html",
        "assets/info/sequencer.html",
        "assets/src/tutorial.xml"
      ]);
    })
  );
});

//  This is run when an updated version of this file gets control
//  Time to delete any old caches
//  The standard way to do this is by returning a Promise
//  that gets fulfilled when the cache cleanup is done.
self.addEventListener('activate',function(event) {
 event.waitUntil(
    caches.keys().then(function(cacheNames) {
      return Promise.all(
        cacheNames.filter(cacheName => cacheName != self.version)
                  .map(cacheName => caches.delete(cacheName) )
      );
    })
  );
});

//  This is run whenever the browser loads a page from the Taminations domain
self.addEventListener('fetch', function(event) {
  event.respondWith(caches.match(event.request).then(function(response) {
    // caches.match() always resolves
    // but in case of success response will have value
    if (response !== undefined) {
      return response;
    } else {
      return fetch(event.request).then(function (response) {
        // response may be used only once
        // we need to save clone to put one copy in cache
        // and serve second one
        let responseClone = response.clone();
        let clone2 = response.clone();
        caches.open(self.version).then(function (cache) {
          cache.put(event.request, responseClone);
        });
        return response;
      }).catch(function () {
        console.log("Unable to fetch "+event.request.url);
        //  Should really look at the url and return a default of the appropriate type
        return caches.match('tam120.png');
      });
    }
  }));
});


//  After the client loads the About page, it sends a message
//  requesting the rest of Taminations to be loaded into the cache
//  This method loads all Tamination html and xml files, and
//  reads the html files to load any image files it uses.
//  Promises are used to detect when files have been successfully read
//  and added to the cache.  After all Promises have been fulfilled,
//  a message is sent back to the client so the user can be notified.
self.addEventListener('message', event => {
  var data = event.data;
  if (data.command == "Version Number") {
    self.port = event.ports[0];
    self.port.postMessage({"message":"Report version","version":self.version});
  }
  if (data.command == "Load All Files") {
    console.log("Loading all files");
    caches.open(self.version).then( cache =>
      cache.add("assets/src/calls.xml").then( () =>
        fetch("assets/src/calls.xml").then( response =>
          response.text().then( str => {
            //  Unfortunately XML parsing is not available to service workers
            //  so we have to do this the hard way
            var re = /link="([^"]+)"/g;
            var match = re.exec(str);
            var thesePromises = [];
            while (match != null) {
              let link = match[1];
              thesePromises.push(cache.add("assets/"+link+".xml").then( ()=>{ },()=>{
                console.log("Error loading "+link+".xml")
              }));
              let html = "assets/" + link + ".html";
              thesePromises.push(cache.add(html).then( () => {
                //  Also look for images linked by html pages
                let base = html.replace(/[^\/]+\.html/,"");
                return fetch(html).then( response2 =>
                  response2.text().then( str2 => {
                    var thosePromises = [];
                    var re2 = /src="([^"]+)"/g;
                    var match2 = re2.exec(str2);
                    while (match2 != null) {
                      let link2 = match2[1];
                      thosePromises.push(cache.add(base+link2).then( ()=>{ },
                        () => console.log("Error loading "+base+link2)
                      ));
                      match2 = re2.exec(str2);
                    }
                    return Promise.all(thosePromises);
                  })
                );
              }), () => console.log("Error loading "+link+".xml") );
              match = re.exec(str);
            }  // end while
            return Promise.all(thesePromises);
          })
        )
      )
    ).then( () => {
        console.log("All files loaded!");
        self.port.postMessage({"message":"All files loaded", "version":self.version});
    })
  }
});
