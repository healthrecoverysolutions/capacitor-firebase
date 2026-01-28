# Building Guide

This guide covers how to build the `@healthrecoverysolutions/capacitor-firebase-messaging` package from source.

## Prerequisites

Before building, ensure you have:

1. **Node.js**: Version 16.x or higher
   ```bash
   node --version  # Should be v16.x or higher
   ```

2. **npm**: Version 8.x or higher
   ```bash
   npm --version   # Should be v8.x or higher
   ```

3. **Repository Cloned**: Clone the repository and navigate to it
   ```bash
   git clone https://github.com/healthrecoverysolutions/capacitor-firebase.git
   cd capacitor-firebase
   ```

## Build Process Overview

The build process includes:
1. **TypeScript Compilation**: Converts TypeScript source to JavaScript
2. **Documentation Generation**: Auto-generates API documentation
3. **Bundling**: Creates distribution bundles using Rollup
4. **Type Definitions**: Generates TypeScript declaration files

## Quick Build

To build the messaging package quickly:

```bash
cd packages/messaging
npm install
npm run build
```

## Detailed Build Steps

### 1. Install Root Dependencies

From the repository root:

```bash
npm install
```

This installs workspace dependencies and tools like Turbo.

### 2. Navigate to Messaging Package

```bash
cd packages/messaging
```

### 3. Install Package Dependencies

```bash
npm install
```

This installs:
- **Development dependencies**: TypeScript, Rollup, ESLint, Prettier, etc.
- **Peer dependencies**: Capacitor and Firebase SDKs
- **Build tools**: Documentation generator, bundlers

### 4. Run the Build

```bash
npm run build
```

This executes the build script which:

1. **Cleans** the `dist` directory:
   ```bash
   rimraf ./dist
   ```

2. **Generates documentation** from JSDoc comments:
   ```bash
   docgen --api FirebaseMessagingPlugin --output-readme README.md --output-json dist/docs.json
   ```

3. **Compiles TypeScript**:
   ```bash
   tsc
   ```

4. **Bundles with Rollup**:
   ```bash
   rollup -c rollup.config.mjs
   ```

## Build Output

After a successful build, the `dist` directory contains:

```
dist/
├── docs.json              # API documentation in JSON format
├── esm/                   # ES Module build
│   ├── definitions.d.ts   # Type definitions for plugin interfaces
│   ├── definitions.js     # Compiled definitions
│   ├── index.d.ts         # Main entry point types
│   ├── index.js           # Main entry point
│   └── web.d.ts          # Web implementation types
│   └── web.js            # Web implementation
├── plugin.cjs.js         # CommonJS bundle
└── plugin.js             # UMD bundle (for unpkg)
```

### Output Files Explained

- **`esm/`**: ES6 modules for modern bundlers (Webpack, Vite, etc.)
- **`plugin.cjs.js`**: CommonJS format for Node.js and older tooling
- **`plugin.js`**: Universal Module Definition (UMD) for CDN usage
- **`docs.json`**: Machine-readable API documentation

## Build Scripts

The `package.json` includes several build-related scripts:

### Core Build Commands

```bash
# Full build (clean + docgen + compile + bundle)
npm run build

# Clean the dist directory
npm run clean

# Watch mode for development (compiles on file changes)
npm run watch

# Generate documentation only
npm run docgen
```

### Verification Commands

```bash
# Run all verification tests (iOS, Android, Web)
npm run verify

# Verify iOS build
npm run verify:ios

# Verify Android build
npm run verify:android

# Verify Web build (same as npm run build)
npm run verify:web
```

### Code Quality Commands

```bash
# Run linting
npm run lint

# Format and fix code
npm run fmt

# Run ESLint only
npm run eslint

# Run Prettier only
npm run prettier -- --check
```

### Platform-Specific Commands

```bash
# Install iOS CocoaPods dependencies
npm run ios:pod:install

# Install iOS Swift Package Manager dependencies
npm run ios:spm:install
```

## Development Workflow

### Watch Mode

For active development, use watch mode to automatically recompile on changes:

```bash
npm run watch
```

This runs TypeScript in watch mode. Note: You'll need to manually run `npm run build` to update documentation and bundles.

### Incremental Build

If you only modified TypeScript files:

```bash
tsc
```

For a complete rebuild:

```bash
npm run build
```

## Building for Different Platforms

### Web Build

The web build is included in the standard build process. The web implementation is in `src/web.ts`.

```bash
npm run verify:web
```

### Android Build

To verify the Android native code builds correctly:

```bash
npm run verify:android
```

This requires:
- Java Development Kit (JDK) 17+
- Android SDK
- Gradle

### iOS Build

To verify the iOS native code builds correctly:

```bash
npm run verify:ios
```

This requires:
- macOS
- Xcode 15+
- CocoaPods

Before running, install dependencies:

```bash
npm run ios:pod:install
```

## Troubleshooting

### Error: Cannot find module 'typescript'

**Cause**: Dependencies not installed.

**Solution**:
```bash
npm install
```

### Error: 'dist' directory not found

**Cause**: Build hasn't been run yet.

**Solution**:
```bash
npm run build
```

### Error: Permission denied

**Cause**: Insufficient file permissions.

**Solution**:
```bash
sudo chown -R $USER:$USER .
npm run build
```

### TypeScript Compilation Errors

**Cause**: Type errors in source code.

**Solution**:
1. Review the error messages
2. Fix type errors in `src/` files
3. Ensure peer dependencies are installed
4. Run `npm install` to update types

### Rollup Bundle Errors

**Cause**: Import/export issues or missing dependencies.

**Solution**:
1. Check `rollup.config.mjs` configuration
2. Verify all imports are correct
3. Ensure external dependencies are properly declared

### Documentation Generation Fails

**Cause**: Invalid JSDoc comments or missing plugin interface.

**Solution**:
1. Check JSDoc comments in `src/definitions.ts`
2. Ensure the plugin interface is properly exported
3. Verify `@capacitor/docgen` is installed

### iOS Build Fails

**Cause**: CocoaPods dependencies not installed or outdated.

**Solution**:
```bash
npm run ios:pod:install
```

### Android Build Fails

**Cause**: Gradle dependencies or Java version issues.

**Solution**:
1. Ensure JDK 17+ is installed:
   ```bash
   java -version
   ```
2. Clean and rebuild:
   ```bash
   cd android
   ./gradlew clean
   ./gradlew build
   ```

## Build Configuration Files

### `tsconfig.json`

Controls TypeScript compilation:
- Target: ES2017
- Module: ES6
- Output: `dist/esm/`

### `rollup.config.mjs`

Controls bundling:
- Input: Compiled TypeScript from `dist/esm/`
- Output: Multiple formats (CJS, UMD)
- Plugins: Node resolution, external dependencies

### `.swiftlint.yml` (iOS)

Swift code linting rules for iOS native code.

### `android/build.gradle`

Android build configuration and dependencies.

## Continuous Integration

### Building in CI/CD

Example GitHub Actions workflow:

```yaml
name: Build and Test

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'

      - name: Install dependencies
        run: npm ci

      - name: Build messaging package
        run: |
          cd packages/messaging
          npm run build

      - name: Run tests
        run: |
          cd packages/messaging
          npm run lint
          npm run verify:web
```

## Build Performance

### Optimization Tips

1. **Use `npm ci`** instead of `npm install` in CI environments
2. **Cache dependencies** in CI to speed up builds
3. **Skip unnecessary steps** during development (use `tsc` instead of full build)
4. **Parallel builds**: If building multiple packages, use Turbo:
   ```bash
   # From repository root
   npm run build
   ```

### Build Times

Typical build times on modern hardware:
- **Clean build**: 10-20 seconds
- **Incremental build**: 2-5 seconds
- **Documentation generation**: 2-3 seconds
- **iOS verification**: 30-60 seconds
- **Android verification**: 20-40 seconds

## Testing the Build

After building, verify the output:

```bash
# Check that all files exist
ls -la dist/

# Verify the package can be packed
npm pack --dry-run

# Test the package locally
npm link
cd /path/to/test/project
npm link @healthrecoverysolutions/capacitor-firebase-messaging
```

## Next Steps

After building:
1. **Run tests**: `npm run verify`
2. **Check code quality**: `npm run lint`
3. **Test locally**: Link the package to a test project
4. **Publish**: See [PUBLISHING.md](PUBLISHING.md) for publishing instructions

## Additional Resources

- [TypeScript Compiler Options](https://www.typescriptlang.org/tsconfig)
- [Rollup Documentation](https://rollupjs.org/)
- [Capacitor Plugin Development](https://capacitorjs.com/docs/plugins/creating-plugins)
- [Capacitor iOS Guide](https://capacitorjs.com/docs/ios)
- [Capacitor Android Guide](https://capacitorjs.com/docs/android)