
# Photo Scanner Documentation

*Last update: April 21, 2025*

Welcome to the Photo Scanner documentation! This repository contains comprehensive guides for both users and developers of the Photo Scanner application.

## Documentation Workflow

This documentation follows a specialized workflow:

1. **Source of Truth**: All documentation is maintained in the `help/` directory within the dedicated `documentation` branch.

2. **Automatic Publishing**: When changes are pushed to the documentation branch, a GitHub Action automatically copies these files to the `docs/` directory in the `master` branch.

3. **User Access**: App users access this documentation through the app's help system, which links to the files in the `docs/` directory on GitHub.

## About Photo Scanner

Photo Scanner is a mobile application designed to help users capture, process, and organize photographs. It's perfect for digitizing physical photos, documents, and other printed materials with your smartphone camera.

## Documentation Structure

This documentation is organized into two main sections:

### User Guide

The User Guide is designed for end-users of the Photo Scanner application. It provides detailed instructions on how to use the various features of the app, including:

- Getting started with Photo Scanner
- Taking and processing photos
- Managing your photo collection
- Using advanced features like metadata management
- Troubleshooting common issues

### Developer Guide

The Developer Guide is for contributors who want to understand, modify, or extend the Photo Scanner application. It covers:

- Project architecture and design
- Build and deployment instructions
- Guidelines for contributing to the project
- API documentation

## Using This Documentation

You can navigate through the documentation using the sidebar on GitBook, or by following the links in the [SUMMARY.md](SUMMARY.md) file.
## Contributing to Documentation

We welcome contributions to improve this documentation! To contribute:

1. Ensure you're working on the `documentation` branch:
   ```bash
   git checkout documentation
   ```

2. Make your changes to the relevant Markdown files in the `help/` directory (not the `docs/` directory)

3. Commit and push your changes to the documentation branch:
   ```bash
   git add help/
   git commit -m "Update documentation for [feature]"
   git push origin documentation
   ```

4. The GitHub Action will automatically update the master branch

For more detailed instructions, see our [CONTRIBUTING guide](CONTRIBUTING.md).

## Publishing with GitBook

This documentation is designed to be published using GitBook. For information on how to set up and configure GitBook with this documentation, see the [Publishing Guide](developer-guide/publishing.md).

## License

This documentation is licensed under the same license as the Photo Scanner application. See the LICENSE file in the root directory for details.
