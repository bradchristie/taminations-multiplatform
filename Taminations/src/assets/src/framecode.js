//  Highlight current part
function setPart(part,currentcall)   {
  var nodes = document.getElementsByTagName('span');
  for (var i=0; i<nodes.length; i++) {
    var elem = nodes.item(i);
    var classstr = ' '+elem.className+' ';
    //  Remove any previous highlight
    classstr = classstr.replace('definition-highlight','');
    //  Highlight based on either class or id
    var teststr = ' '+classstr+' '+elem.id+' ';
    //  Highlight either 'Part'nn or Callnn
    if (teststr.indexOf(' '+currentcall+part+' ') > 0 ||
        teststr.indexOf('Part'+part+' ') > 0)
      classstr += 'definition-highlight';
    //  Make sure we don't constantly grow spaces in the class
    classstr = classstr.replace(/^\\s+|\\s+$/g,'');
    elem.className = classstr;
  }
}

//  Function to show either full or abbrev
function setAbbrev(isAbbrev) {
  var found = 'false';
  var nodes = document.getElementsByTagName('*');
  for (var i=0; i<nodes.length; i++) {
    var elem = nodes.item(i);
    if (elem.className.indexOf('abbrev') >= 0) {
      elem.style.display = isAbbrev ? '' : 'none';
      found = 'true';
    }
    if (elem.className.indexOf('full') >= 0) {
      elem.style.display = isAbbrev ? 'none' : '';
      found = 'true';
    }
  }
  //  Return value used to hide/show buttons
  return found;
}

//  Show platform-specific text
function showPlatformElements(platform) {
  //console.log("Showing platform "+platform)
  var nodes = document.getElementsByClassName(platform)
  for (var i=0; i<nodes.length; i++) {
    var elem = nodes.item(i);
    elem.style.display = 'initial';
  }
}

function allFilesLoaded(version) {
  document.getElementById("before-load").style.display = "none";
  document.getElementById("after-load").style.display = "initial";
  window.localStorage.setItem("all files loaded",version);
}

//  Called when About page is loaded
function loadFilesInBackground() {
  document.getElementById("before-load").style.display = "initial";
  document.getElementById("after-load").style.display = "none";
  //  Version is version number when files were last loaded
  var version = window.localStorage.getItem("all files loaded")
  console.log("Local version: "+version);
  if (navigator.serviceWorker.controller) {
    var messageChannel = new MessageChannel();
    //  There are two messages we receive
    //  First is the version number.  If it matches we don't need to load.
    //  If it does not match, we send a request to load and wait
    //  for the completion message.
    messageChannel.port1.onmessage = function(event) {
      console.log("Message, version: "+event.data.message+" "+event.data.version);
      if (event.data.message == "All files loaded" || event.data.version == version)
        allFilesLoaded(event.data.version);
      else
        navigator.serviceWorker.controller.postMessage({
           "command": "Load All Files",
         });
    }
    console.log("Requesting cache version number");
    navigator.serviceWorker.controller.postMessage({
           "command": "Version Number",
       }, [messageChannel.port2]);
  }
}
