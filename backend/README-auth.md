GitHub OAuth configuration

- Add the following properties to `application.properties` or environment variables (recommended):
  - `github.client-id` - GitHub OAuth App Client ID
  - `github.client-secret` - GitHub OAuth App Client Secret
  - `github.redirect-uri` - (optional) Redirect URI registered in GitHub App (default: `http://localhost:8080/api/oauth/github/callback`)
  - `frontend.redirect-uri` - (optional) Frontend SPA URL to redirect after successful GitHub login (default: `http://localhost:5173`)

Flow:

- Frontend calls `/sys/user/oauth/github/authorize` to get the GitHub authorization URL and redirects the user there.
- GitHub redirects back to `/api/oauth/github/callback` with a `code` parameter.
- The backend exchanges the `code` for an access token, fetches GitHub user info, creates or finds a local user (`username = github:{login}`), and redirects the browser to the frontend with `#token={jwt}`.

Notes:

- This is a simplified implementation intended to get OAuth working end-to-end. For production use, verify scope, handle multiple emails, secure redirect URIs, add state parameter to protect against CSRF, and persist external OAuth provider info.
