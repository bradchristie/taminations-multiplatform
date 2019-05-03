
//  This version number is reported to the client.  If it does not match
//  the version number saved when files were downloaded, the client
//  will request a new download to fill the new cache.
self.version = "1.5.24";
//  This is run just once, when the user loads Taminations
self.addEventListener('install', function(event) {
  event.waitUntil(
    caches.open(self.version).then(function(cache) {
      return cache.addAll([
         //  These are the files needed to start up Taminations
         //  or are otherwise not loaded in the cache by the code below
        "index.html",
        "lib/kotlin.js",
        "lib/kotlinx-coroutines-core.js",
        "main/Taminations.js",
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

//  Parse the links out of calls.xml and return a Promise array
function getCallLinks(cache,str) {
  var re = /link="([^"]+)"/g;
  var match = re.exec(str);
  var thesePromises = [];
  while (match != null) {
    let xml = "assets/"+match[1]+".xml";
    let htmllink = "assets/"+match[1]+".html";
    thesePromises.push(fetch(htmllink)
                    .then(response => {
                       cache.put(htmllink,response.clone())
                       return response;
                     })
                    .then( response => response.text())
                    .then( html => getHTMLLinks(cache,htmllink,html))
                    .then(cache.add(xml))
                    .catch( err => {
                        console.log("Error loading "+link+"  "+err);
                        return Promise.reject(err);
                     }))
    match = re.exec(str);
  }
  return Promise.all(thesePromises);
}

//  Parse links from calls.xml, create a Promise array
//  to fetch the HTML and scan for links to images
//  Unfortunately XML parsing is not available to service workers
//  so we have to do this the hard way
function getHTMLLinks(cache,link,html) {
  var base = link.replace(/[^\/]+\.html/,"");
  var thosePromises = [];
  var re2 = /src="([^"]+)"/g;
  var match2 = re2.exec(html);
  while (match2 != null) {
    let link2 = match2[1];
    //  Every html file loads framecode.js
    //  Avoid repeated requests to cache that file
    if (link2 != "../src/framecode.js") {
          thosePromises.push(cache.add(base+link2).catch(
              err => console.log("Error loading "+base+link2+" "+err)
      ));
    }
    match2 = re2.exec(html);
  }
  return Promise.all(thosePromises);
}

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
    caches.open(self.version).then( cache => {
      console.log("Opened cache "+self.version);
      cache.add("assets/src/calls.xml")
           .then( _ => fetch("assets/src/calls.xml"))
           .then( response => response.text())
           .then( str => getCallLinks(cache,str) )
           .then( function() {
        console.log("All files loaded!");
        self.port.postMessage({"message":"All files loaded", "version":self.version});
      }, function(err) {
        console.log("Something failed, files not loaded: "+err);
        self.port.postMessage({"message":"Something failed, files not loaded", "version":self.version});
      });
    })
  }
});
