# SCWC and HCWC proxy

This is a simple nginx setup to pass TCP traffic through to cwc.house.gov and soapbox.senate.gov, which only allow traffic from a whitelisted IP. SSL Requests intended for cwc.house.gov and soapbox.senate.gov will be forwarded along to the appropriate endpoint.

1. Provision a ec2 instance using a ubuntu ami
   - Make sure the ec2 instance has the proper security group rules to accept inbound connection on port 443
1. Copy the `nginx.conf` over to the machine.
   ```
   scp -i <private_key> nginx.conf ubuntu@<hostname>:~/nginx.conf
   ```
1. SSH into the instance
   ```
   ssh -i <private_key> ubuntu@<hostname>
   ```
1. Install nginx with the stream module and replace the default nginx configuration
   ```
   sudo bash
   apt update
   apt install nginx-full
   mv /home/ubuntu/nginx.conf /etc/nginx/nginx.conf
   systemctl reload nginx
   ```
1. Assign the whitelisted Elastic IP to the ec2 instance

## References

- https://www.digitalocean.com/community/tutorials/how-to-install-nginx-on-ubuntu-20-04
- https://www.eigenmagic.com/2021/09/20/nginx-as-a-reverse-stream-proxy/
