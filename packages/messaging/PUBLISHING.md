# Publishing Guide

This guide covers how to publish the `@healthrecoverysolutions/capacitor-firebase-messaging` package to GitHub Packages.

## Prerequisites

Before publishing, ensure you have:

1. **GitHub Personal Access Token (PAT)** with the following permissions:
   - `write:packages` - to publish packages
   - `read:packages` - to download packages
   - `repo` - to access repository information

   To create a token:
   - Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
   - Click "Generate new token (classic)"
   - Select the required scopes
   - Generate and save the token securely

2. **Repository Access**: Write access to the `healthrecoverysolutions/capacitor-firebase` repository

3. **Node.js and npm**: Ensure you have Node.js 16+ and npm installed

## Setup Authentication

### Option 1: Global Authentication (Recommended for Development)

Create or edit `~/.npmrc` in your home directory:

```bash
//npm.pkg.github.com/:_authToken=YOUR_GITHUB_TOKEN
@healthrecoverysolutions:registry=https://npm.pkg.github.com
```

Replace `YOUR_GITHUB_TOKEN` with your actual GitHub Personal Access Token.

### Option 2: Project-Level Authentication

Create or edit `.npmrc` in the project root (already configured):

```bash
@healthrecoverysolutions:registry=https://npm.pkg.github.com
```

Then set the auth token as an environment variable:

```bash
echo "//npm.pkg.github.com/:_authToken=${GITHUB_TOKEN}" >> .npmrc
```

**Important**: If using project-level `.npmrc`, add it to `.gitignore` to avoid committing tokens.

### Option 3: Using npm login

```bash
npm login --scope=@healthrecoverysolutions --registry=https://npm.pkg.github.com
```

When prompted:
- **Username**: Your GitHub username
- **Password**: Your GitHub Personal Access Token (PAT)
- **Email**: Your GitHub email

## Publishing Steps

### 1. Navigate to the Messaging Package

```bash
cd packages/messaging
```

### 2. Update Version (if needed)

Before publishing, update the version in `package.json` following [Semantic Versioning](https://semver.org/):

```bash
npm version patch  # For bug fixes (8.0.1 → 8.0.2)
npm version minor  # For new features (8.0.1 → 8.1.0)
npm version major  # For breaking changes (8.0.1 → 9.0.0)
```

Or manually edit the `version` field in `package.json`.

### 3. Install Dependencies

```bash
npm install
```

### 4. Run Tests and Linting

```bash
npm run lint
npm run verify
```

### 5. Build the Package

```bash
npm run build
```

This will:
- Clean the `dist` directory
- Generate documentation
- Compile TypeScript
- Bundle with Rollup

### 6. Verify Package Contents

Check what will be published:

```bash
npm pack --dry-run
```

This shows all files that will be included in the package.

### 7. Publish to GitHub Packages

```bash
npm publish
```

If successful, you'll see:

```
+ @healthrecoverysolutions/capacitor-firebase-messaging@8.0.1
```

### 8. Verify Publication

Check that the package appears in GitHub Packages:
- Navigate to: `https://github.com/orgs/healthrecoverysolutions/packages`
- Or: `https://github.com/healthrecoverysolutions/capacitor-firebase/packages`

## Installing the Published Package

### For Package Users

Users need to configure their `.npmrc` file:

```bash
@healthrecoverysolutions:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=YOUR_GITHUB_TOKEN
```

Then install:

```bash
npm install @healthrecoverysolutions/capacitor-firebase-messaging
```

### For Capacitor Projects

1. Configure `.npmrc` as shown above
2. Install the package:

```bash
npm install @healthrecoverysolutions/capacitor-firebase-messaging firebase
npx cap sync
```

3. Update imports in your code:

```typescript
import { FirebaseMessaging } from '@healthrecoverysolutions/capacitor-firebase-messaging';
```

## Automated Publishing with GitHub Actions

For CI/CD workflows, you can automate publishing using GitHub Actions.

Create `.github/workflows/publish.yml`:

```yaml
name: Publish to GitHub Packages

on:
  release:
    types: [created]

jobs:
  publish:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          registry-url: 'https://npm.pkg.github.com'
          scope: '@healthrecoverysolutions'

      - name: Install dependencies
        run: npm ci

      - name: Build messaging package
        run: |
          cd packages/messaging
          npm run build

      - name: Publish to GitHub Packages
        run: |
          cd packages/messaging
          npm publish
        env:
          NODE_AUTH_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

## Troubleshooting

### Error: 401 Unauthorized

**Cause**: Invalid or missing authentication token.

**Solution**:
- Verify your GitHub token has `write:packages` permission
- Check that your `.npmrc` file is correctly configured
- Ensure the token hasn't expired

### Error: 404 Not Found

**Cause**: Registry URL is incorrect or package doesn't exist.

**Solution**:
- Verify the package name matches: `@healthrecoverysolutions/capacitor-firebase-messaging`
- Confirm `.npmrc` has the correct registry: `https://npm.pkg.github.com`

### Error: 403 Forbidden

**Cause**: Insufficient permissions to publish to the organization.

**Solution**:
- Verify you have write access to the `healthrecoverysolutions` organization
- Ensure the package doesn't already exist with different permissions

### Error: Package version already exists

**Cause**: Attempting to publish a version that already exists.

**Solution**:
- Increment the version number in `package.json`
- Use `npm version` to bump the version

### Build Errors

**Cause**: Missing dependencies or compilation errors.

**Solution**:
- Run `npm install` to ensure all dependencies are installed
- Check TypeScript compilation errors
- Verify all peer dependencies are met

## Version Management

When publishing updates:

1. **Patch Release** (8.0.1 → 8.0.2): Bug fixes, documentation updates
2. **Minor Release** (8.0.1 → 8.1.0): New features, non-breaking changes
3. **Major Release** (8.0.1 → 9.0.0): Breaking changes

Always update `CHANGELOG.md` with release notes before publishing.

## Best Practices

1. **Always test before publishing**: Run `npm run verify` to ensure everything works
2. **Document changes**: Update `CHANGELOG.md` and `README.md`
3. **Version appropriately**: Follow semantic versioning
4. **Review package contents**: Use `npm pack --dry-run` before publishing
5. **Tag releases**: Create Git tags for published versions
6. **Communicate breaking changes**: Update `BREAKING.md` for major versions

## Additional Resources

- [GitHub Packages Documentation](https://docs.github.com/en/packages)
- [npm Publishing Guide](https://docs.npmjs.com/cli/v10/commands/npm-publish)
- [Semantic Versioning](https://semver.org/)
- [Capacitor Plugin Guide](https://capacitorjs.com/docs/plugins)