#!/usr/bin/python
""" cherrypy_example.py

    COMPSYS302 - Software Design
    Author: Andrew Chen (andrew.chen@auckland.ac.nz)
    Last Edited: 19/02/2015

    This program uses the CherryPy web server (from www.cherrypy.org).
"""
# Requires:  CherryPy 3.2.2  (www.cherrypy.org)
#            Python  (We use 2.7)
from telnetlib import theNULL

# The address we listen for connections on
listen_ip = "0.0.0.0"
listen_port = 10030

import cherrypy
import time
import threading
import urllib2
import os
import hashlib
import socket


class MainApp(object):

    # CherryPy Configuration
    _cp_config = {'tools.encode.on': True,
                  'tools.encode.encoding': 'utf-8',
                  'tools.sessions.on' : 'True',
                 }                 

    # If they try somewhere we don't know, catch it here and send them to the right place.
    @cherrypy.expose
    def default(self, *args, **kwargs):
        """The default page, given when we don't recognise where the request is for."""
        Page = """<html lang="en">
        <head>
            <title>Home Page</title>
            <style>
                * {font-family:"Courier New";
                      color:white;}
                input {color:black;}
                body {background-color:48004D;}
            </style>
        </head>
        <body>
            <h1>404: Page not found.</h1>
            <p>I don't know where you're trying to go, so have a 404 Error.</p>
        </body>
        </html>
        """
        cherrypy.response.status = 404
        return Page
    
    @cherrypy.expose   
    def pingElse(self, username=None):
        Page = """<html lang="en">
        <head>
            <title>Home Page</title>
            <style>
                * {font-family:"Courier New";
                      color:white;}
                input {color:black;}
                body {background-color:48004D;}
            </style>
        </head>
        <body>
        """
        try:
            strArray = self.getListArray()
            print strArray
            for j in range(0, len(strArray)):
                if strArray[j][0] == username:
                    i = j
            url = 'http://' + str(strArray[i][2]) + ":" + str(strArray[i][3]) + "/ping?sender=klai054"
            response = urllib2.urlopen(url, timeout=3)
            the_page = response.read()
            return Page + "<h1>Ping Request...</h1><p>" + the_page + "</p></body></html>"
        except:
            return Page + "<h1>Ping Request...</h1><p>Ping Failed...</p></body></html>"
        
    @cherrypy.expose
    def ping(self, sender=None):
        return '0'
    
    @cherrypy.expose
    def listAPI(self):
        req = urllib2.Request('http://cs302.pythonanywhere.com/listAPI')
        response = urllib2.urlopen(req)
        the_page = response.read()
        return the_page + """<br/>
        Click here to go <a href='index'>back</a> to home page.
        """
    
    @cherrypy.expose
    def index(self):
        Page = """
        <html lang="en">
        <head>
            <title>Home Page</title>
            <style>
                * {font-family:"Courier New";
                      color:white;}
                input {color:black;}
                body {background-color:48004D;}
            </style>
        </head>
        <body>
        <h1 style="text-align:center">Welcome!</h1>
        <p style="text-align:center">This is the web-page for Group 30 in COMPSYS302!<br/>
        This web-page is the main hub the game "Tank Combat" online play.<br> 
        """
        try:
            Page += "Hello <b><u>" + cherrypy.session['username'] + "</u></b> ! <a href='signout'>log-off</a>."
            Page += "<br>Click <a href='getList'>here</a> to get list of online users."
        except KeyError:  # There is no username
            Page += "Click <a href='login'>here</a> to login."
        
        Page += """<br><p style="text-align:right">Click <a href='listAPI'>here</a> to list API's.<br><br></p>
            
        </body>
        </html>
        """
        return Page
    
    @cherrypy.expose
    def showSession(self):
        Page = "Username: " + str(cherrypy.session.get('username'))
        Page += "<br>Password: " + str(cherrypy.session.get('password'))
        Page += "<br>Location: " + str(cherrypy.session.get('location'))
        return Page
    
    @cherrypy.expose
    def login(self):
        print os.path.dirname(os.path.realpath(__file__))
        Page = open("login.html")
        return Page
    
    @cherrypy.expose    
    def getList(self):
        Page = """
        <html lang="en">
        <head>
            <title>List Users</title>
            <style>
                * {font-family:"Courier New";
                      color:white;}
                input {color:black;}
                button {color:black;}
                body {background-color:48004D;}
            </style>
        </head>
        <body>
        """
        strArray = self.getListArray()
        if strArray == 1 :
            return Page + """<h1>Get Failed</h1><br>
             Click <a href='index'>here</a> to go back to the home page.
             <br> Click <a href='login'>here</a> to try log in.
             </body>
             </html>
            """
        else:
            print strArray  # show list of users online
            Page += """<h1>Online Users</h1>
            <p>Here are a list of currently online users:</p><ul>"""
            for i in range(0, len(strArray)):
                
                Page += "<li type = 'square'><b>Name: </b>" + strArray[i][0] + "<b> - Location: </b>" + strArray[i][1] + "<b> - IP & Port: </b>" + strArray[i][2] + ":" + strArray[i][3]        
                Page += '<form action="/pingElse" method="post" enctype="multipart/form-data">'
                Page += '<button name="username" type="submit" value="{}">Ping</button></form>'.format(strArray[i][0])
                Page += '<form action="/challenge" method="post" enctype="multipart/form-data">'
                Page += '<button name="username" type="submit" value="{}">Challenge</button></form>'.format(strArray[i][0])
                Page += '</li>'
            
            print Page
            return Page + """</ul><br/>
            Click <a href='index'>here</a> to go back to the home page.
            </body>
            </html>
            """
    
    def getListArray(self):
        url = "http://cs302.pythonanywhere.com/getList?username=" + str(cherrypy.session.get('username')) + "&password=" + str(cherrypy.session.get('password'))
        req = urllib2.Request(url)
        response = urllib2.urlopen(req)
        the_page = response.read()        
        
        if (the_page[0] != "0"):
            return 1        
        
        string = the_page[30:]  # remove net code
        string = string.replace("\r\n", " ")
        string = string[:(len(string) - 1)]
        x = 1; i = 0; strArray = []  # initialize variables
        while x != 0:  # split list into 2d list
            x = string.find(" ")
            if x == -1:  # last user
                strArray.append([])
                for j in range(1, 5):
                    strArray[i].append(string[:string.find(",")])
                    string = string[string.find(",") + 1:]
                strArray[i].append(string)
            else:  # non last user
                strArray.append([])
                tStr = string[:x]
                for j in range(1, 5):
                    strArray[i].append(tStr[:tStr.find(",")])
                    tStr = tStr[tStr.find(",") + 1:]
                strArray[i].append(tStr)
                string = string[(x + 1):]
            x += 1
            i += 1
        return strArray
    
    @cherrypy.expose
    def report(self, username, password, location, ip):
        try:
            while (self.logged):
                print "refreshing log in..."
                print "user: " + username
                hashed_password = password
                url = "http://cs302.pythonanywhere.com/report?username=" + str(username) + "&password=" + str(hashed_password) + "&ip=" + str(ip) + "&port=" + str(listen_port) + "&location=" + str(location)
                req = urllib2.Request(url)
                urllib2.urlopen(req)
                time.sleep(30)
            pass
        except:
            pass
    
    
    # LOGGING IN AND OUT
    @cherrypy.expose
    def signin(self, username=None, password=None):
        """Check their name and password and send them either to the main page, or back to the main login screen."""
        cherrypy.session['username'] = username
        hashed_password = hashlib.sha256(password + "COMPSYS302-2016").hexdigest()
        cherrypy.session['password'] = hashed_password
        print "Username: " + cherrypy.session['username']
        print "Password >>> Hashed: " + password + " >>> " + cherrypy.session['password']
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(('google.com', 0))
        ip = s.getsockname()[0]
        cherrypy.session['ip'] = ip
        print "IP: " + str(cherrypy.session['ip'])
        if str(ip)[:6] == "10.103":
            location = 0
        elif str(ip)[:6] == "172.23":
            location = 1
        else:
            location = 2
        print "Location: " + str(location)
        cherrypy.session['location'] = location
        
        url = "http://cs302.pythonanywhere.com/report?username=" + str(username) + "&password=" + str(hashed_password) + "&ip=" + str(ip) + "&port=" + str(listen_port) + "&location=" + str(location)
        req = urllib2.Request(url)
        response = urllib2.urlopen(req)
        the_page = response.read()
        
        Page = """
        <html lang="en">
        <head>
            <title>Signing in...</title>
            <style>
                * {font-family:"Courier New";
                      color:white;}
                input {color:black;}
                body {background-color:48004D;}
            </style>
        </head>
        <body>
        """
        if the_page[0] == "0":
            self.logged = True
            self.reportThread = threading.Thread(target=self.report, args=(username, hashed_password, location, ip))
            self.reportThread.start()
            
            return Page + "<h1>Login Successfull!</h1>" + the_page + """<br>
             Click <a href='index'>here</a> to go back to the home page.
             </body>
             </html>
             """
        else:
            self.logged = False
            cherrypy.lib.sessions.expire()
            return Page + "<h1>Login Fail</h1>" + the_page + """<br>
             Click <a href='index'>here</a> to go back to the home page.
             <br> Click <a href='login'>here</a> to try log in again.
             </body>
             </html>
            """
        
    @cherrypy.expose
    def signout(self):
        """Logs the current user out, expires their session"""
        username = cherrypy.session.get('username')
        self.logged = False
        if (username == None):
            pass
        else:
            url = "http://cs302.pythonanywhere.com/logoff?username=" + str(cherrypy.session.get('username')) + "&password=" + str(cherrypy.session.get('password'))
            req = urllib2.Request(url)
            response = urllib2.urlopen(req)
            the_page = response.read()
            cherrypy.lib.sessions.expire()
        raise cherrypy.HTTPRedirect('/')
          
def runMainApp():
    # Create an instance of MainApp and tell Cherrypy to send all requests under / to it. (ie all of them)
    cherrypy.tree.mount(MainApp(), "/")
    
    # Tell Cherrypy to listen for connections on the configured address and port.
    cherrypy.config.update({'server.socket_host': listen_ip,
                            'server.socket_port': listen_port,
                            'engine.autoreload.on': True,
                           })

    print "========================="
    print "University of Auckland"
    print "COMPSYS302 - Software Design Application"
    print "Group 30 - Team Solo Carry"
    print "========================================"                       
    
    # Start the web server
    cherrypy.engine.start()

    # And stop doing anything else. Let the web server take over.
    cherrypy.engine.block()
 
# Run the function to start everything
runMainApp()
