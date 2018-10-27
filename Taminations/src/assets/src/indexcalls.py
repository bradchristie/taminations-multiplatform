
import glob
import re
import sys
import xml.etree.ElementTree as ET
import os.path

def checkversion():
    if sys.version_info[0] != 3:
        print('Not Python 3!')
        sys.exit(1)
    if sys.platform.startswith('win32'):
        sys.stderr.write('Do not use win python!\n')
        sys.exit(1)

def main():
    leveldict = {'b1':{'level':'Basic and Mainstream','sublevel':'Basic 1','order':'A'},
                 'b2':{'level':'Basic and Mainstream','sublevel':'Basic 2','order':'B'},
                 'ms':{'level':'Basic and Mainstream','sublevel':'Mainstream','order':'C'},
                 'plus':{'level':'Plus','sublevel':'Plus','order':'D'},
                 'a1':{'level':'Advanced','sublevel':'A-1','order':'E'},
                 'a2':{'level':'Advanced','sublevel':'A-2','order':'F'},
                 'c1':{'level':'Challenge','sublevel':'C-1','order':'G'},
                 'c2':{'level':'Challenge','sublevel':'C-2','order':'H'},
                 'c3a':{'level':'Challenge','sublevel':'C-3A','order':'I'},
                 'c3b':{'level':'Challenge','sublevel':'C-3B','order':'J'}
                 }
    #  Build table of calls in each file
    r = re.compile(r'tam\s+title="(.*?)"')
    r2 = re.compile(r'/(b1|b2|ms|plus|a1|a2|c1|c2|c3a|c3b)/')
    r3 = re.compile(r'Call\.classes\.(.+?)\s*=')
    r4 = re.compile(r'[^ A-Za-z0-9]')
    r5 = re.compile(r'\(.*?\)\s*')
    r6 = re.compile(r'\.(lang-\w*)\.html')
    #  Start a new xml document for the output
    newtree = ET.ElementTree(ET.Element('calls'))
    newroot = newtree.getroot()
    #  First add in all the explicit calls from the
    #  hand-written calls.xml
    calldict = {}
    tree = ET.parse('calls.xml')
    for call in tree.getroot().findall('call'):
        if 'link' not in call.attrib:
            continue
        if call.attrib['sublevel'] not in leveldict:
            continue
        title = re.sub(r5,'',call.attrib['title']).replace('"','').strip()
        link = call.attrib['link'].replace('.html','')
        order = leveldict[call.attrib['sublevel']]['order']
        if not r2.search(link):
            continue
        calldict[title+'  '+order+'  '+link] = {
            'title':call.attrib['title'],
            'link':link,
            'level':call.attrib['level'],
            'sublevel':call.attrib['sublevel'] }

    #  Read animations from xml files
    for filename in glob.glob('../*/*.xml'):
        m = r2.search(filename)
        if not m:
            continue
        sublevel = m.group(1)
        tree = ET.parse(filename)
        root = tree.getroot()
        if root.tag != 'tamination':
            continue
        link = filename.lstrip('./').replace('.xml','')
        #  Find the languages the definition is translated
        lang = []
        for translation in glob.glob(filename.replace('.xml','')+'*.html'):
            m6 = r6.search(translation)
            if (m6):
                lang += [m6.group(1)]
        lang = ' '.join(lang)
        #  Add the main title, which could be different from the animations
        #  esp for concepts
        title = re.sub(r5,'',root.attrib['title']).replace('"','').strip()
        order = leveldict[sublevel]['order']
        calldict[title+'  '+order+'  '+link] = {
            'title':title,
            'link':link,
            'text':re.sub(r4,'',title.lower()).replace(' ',''),
            'level':leveldict[sublevel]['level'],
            'sublevel':leveldict[sublevel]['sublevel']
        }
        audiofile = sublevel + "/" + re.sub(r4,'',title.lower()).replace(' ','_') + ".mp3"
        if os.path.exists('../'+audiofile):
            calldict[title+'  '+order+'  '+link]['audio'] = audiofile
        if lang:
            calldict[title+'  '+order+'  '+link]['languages'] = lang
        #  Loop through all the animations adding one entry for each
        #  For a lot of calls, these will be the same
        #  But for some they will be different
        for tam in root.findall('tam'):
            title = re.sub(r5,'',tam.attrib['title']).replace('"','').strip()
            if title+'  '+order+'  '+link not in calldict:
                calldict[title+'  '+order+'  '+link] = {
                    'title':title,
                    'link':link,
                    'text':re.sub(r4,'',title.lower()).replace(' ',''),
                    'level':leveldict[sublevel]['level'],
                    'sublevel':leveldict[sublevel]['sublevel']
                }
                audiofile = sublevel + "/" + re.sub(r4,'',title.lower()).replace(' ','_') + ".mp3"
                if os.path.exists('../'+audiofile):
                    calldict[title+'  '+order+'  '+link]['audio'] = audiofile
    #  Sort the results
    calllist = list(calldict.keys())
    calllist.sort(key=str.lower)

    #  Create the output XML
    for call in calllist:
        c = ET.SubElement(newroot,'call')
        for att in calldict[call]:
            c.set(att,calldict[call][att])

    #  Pretty-print the results
    print('<?xml version="1.0"?>')
    print('<!DOCTYPE calls SYSTEM "calls.dtd">')
    print('<!--  This file was generated by indexcalls.py  -->')
    print('<calls>')
    for child in newroot:
        print('  '+ET.tostring(child,encoding="unicode"))
    print('</calls>')

checkversion()
main()
