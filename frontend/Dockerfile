# App Building phase --------
FROM node:alpine3.14

RUN mkdir -p /app
COPY package*.json ./

WORKDIR /app

RUN npm install
COPY . .

RUN npm run build
# End App Building phase --------

# Set host to localhost / the docker image
ENV REACT_HOST=0.0.0.0

# Set app port
ENV REACT_PORT=$PORT

# Set the base url
ENV PROXY_API=$PROXY_API

# Set the browser base url
ENV PROXY_LOGIN=$PROXY_LOGIN

# Start the app
CMD [ "npm", "start" ]
