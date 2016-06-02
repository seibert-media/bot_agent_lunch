# Lunch Bot

## Build

`mvn install`

## Run local

`nsqlookupd`

`nsqd --lookupd-tcp-address=127.0.0.1:4160`

```
NSQ_LOOKUPD_ADDRESS=localhost:4161 \
NSQD_ADDRESS=localhost:4151 \
java -jar target/lunch-bot-jar-with-dependencies.jar
```

or 

```
export NSQ_LOOKUPD_ADDRESS=localhost:4161
export NSQD_ADDRESS=localhost:4151
java -jar target/lunch-bot-jar-with-dependencies.jar
```

## Send Testmessage

```
curl -d '{"message":"/lunch"}' 'http://127.0.0.1:4151/put?topic=bot-request'
```

## Copyright and license

Copyright (c) 2016, Benjamin Borbe, Sandro Janusch
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above
     copyright notice, this list of conditions and the following
     disclaimer in the documentation and/or other materials provided
     with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

