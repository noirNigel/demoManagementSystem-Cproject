# GitHub Connectivity Troubleshooting

If you see errors such as `Failed to connect to github.com port 443`, Git cannot reach GitHub over HTTPS. Try the steps below in order:

1. **Check your network**
   - Confirm you can open https://github.com in a browser from the same machine/network.
   - Ensure VPN or firewall rules are not blocking outbound HTTPS traffic.

2. **Configure a proxy if required**
   - If your network needs a proxy, set it for Git:
     ```sh
     git config --global http.proxy http://<proxy-host>:<proxy-port>
     git config --global https.proxy http://<proxy-host>:<proxy-port>
     ```
   - Remove proxy settings when they are no longer needed:
     ```sh
     git config --global --unset http.proxy
     git config --global --unset https.proxy
     ```

3. **Use SSH instead of HTTPS**
   - Switch the remote URL:
     ```sh
     git remote set-url origin git@github.com:noirNigel/demoManagementSystem-Cproject.git
     ```
   - Make sure your SSH key is added to the SSH agent and to your GitHub account.

4. **Retry with a different network**
   - If possible, test from a mobile hotspot or another network to rule out local blocking.

These steps typically resolve connectivity issues without changing application code.
