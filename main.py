#!/usr/bin/env python
# -*- coding: utf-8 -*-
#
#  main.py
#  
#  Copyright 2018 Tim Ward <scores-man@scoresman-Lenovo-YOGA-710-11IKB>
#  
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#  
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#  
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
#  MA 02110-1301, USA.
#  
#  

import email
import os

def main(args):
    
    directory = "trec07p/data"
    for filename in os.listdir(directory):
        #print("HUGGING BONERS: ", filename)
        #sys.exit()
    
    
        #a = "trec07p/data/inmail.1"
        data = ""
        with open(directory + '/' + filename, encoding="utf8", errors='ignore') as myfile:
            data=myfile.read()#.encode('utf-8')#.strip() #.replace('\n', '')
    
        #print("WAY DATAINGS: ", data, "\n\n\n")

        b = email.message_from_string(data)
    
        body = ""
        if b.is_multipart():
            for part in b.walk():
                ctype = part.get_content_type()
                cdispo = str(part.get('Content-Disposition'))
                #print("WALKING BONERS: ", ctype, cdispo)
                # skip any text/plain (txt) attachments
                if ctype == 'text/html': # and 'attachment' not in cdispo:
                    body = part.get_payload(decode=True)  # decode
                    from bs4 import BeautifulSoup
                    soup = BeautifulSoup(body, "lxml")
                    tokens = soup.get_text().replace('\n', ' ') #.split(' ')
                    #tokens.strip()
                    #[x for x in tokens if x]
                    #filter(None, tokens)
                    tokens = ' '.join(tokens.split())
                    #print("\nSOUPY BONERS: \"" + tokens +  "\"")
                    #sys.exit()
                    #print("BREAKING BONERS")
                    break
        else:
            body = b.get_payload(decode=True)  # decode
            from bs4 import BeautifulSoup
            soup = BeautifulSoup(body, "lxml")
            tokens = soup.get_text().replace('\n', ' ') #.split(' ')
            print("\nHOGGING BONERS: \"" + tokens +  "\"")
            #sys.exit()
    
        #print("FLOGGING BONERS: ", body)
    return 0

if __name__ == '__main__':
    import sys
    sys.exit(main(sys.argv))
