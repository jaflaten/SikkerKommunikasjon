### Create an `.env.local` file

Contents:

```
REACT_APP_API_URL="http://localhost:[port]/api"
```

replace `[port]` with port for your locally hosted backend server

### Run frontend server locally with

`npm run dev`

`heroku local` will run build compiled with `npm run build`

### environment variables are set within the heroku app

Settings/ConfigVars

### Building on Heroku

Allows Heroku to read subdirectory as rootdirectory. (Useful if deploying frontend and backend as separate apps from same repo on Heroku)

https://github.com/timanovsky/subdir-heroku-buildpack

Result of running `heroku buildpacks` while connected to frontend app

```
heroku buildpacks
 »   Warning: heroku update available from 7.53.0 to 7.59.4.
=== sk-prod-frontend Buildpack URLs
1. https://github.com/timanovsky/subdir-heroku-buildpack
2. heroku/nodejs
```
