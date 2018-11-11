# Building the WebApp

## Building with yarn/npm
1. Before running the app you need to install the dependencies with `yarn install` or `npm install`.
2. Then you can build the webapp via `yarn build` or `npm run build`.

## Development with hot-code-replacement (recommended)
1. Start Merlin:
   1. From Desktop installer (MacOS X, Windows or Linux distribution) or
   2. from the server distribution (without desktop application) through the start script or
   3. inside IDE (Intellij).
2. Configure development mode

   If started inside IDE there is nothing to do further. If started from any distribution:
   1. Open the Web browser (if not started automatically) on merlin server port (default is 8042)
   2. Go to Configuration -> Server -> Expert settings and activate checkbox for web development.
   3. Save settings.
   4. Restart server.
3. Start web server with hot-code-replacement:
   1. Run `yarn start` or `npm start` in terminal.
   2. Open browser (if not started automatically) on yarn/npm port (default is 3000).
   
Now you can modify the Web files directly in the directory ```merlin-webapp``` and
any change is automatically deployed instantly for your web browser.
