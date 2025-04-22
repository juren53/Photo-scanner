# Publishing to GitBook

*Last update: April 21, 2025*

This guide explains how to set up and publish the Photo Scanner documentation to GitBook while managing the content in the private GitHub repository.

## Overview

The Photo Scanner documentation is designed to be:

1. **Managed in GitHub**: All documentation files are stored in the `docs/` directory of the Photo Scanner repository.
2. **Published on GitBook**: The documentation is published to GitBook for a user-friendly reading experience.

This approach allows the team to:
- Keep documentation and code in the same repository
- Use Git for version control of documentation
- Review documentation changes through pull requests
- Automatically publish updated documentation to GitBook

## Prerequisites

Before setting up GitBook integration, ensure you have:

1. A GitHub account with access to the Photo Scanner repository
2. A GitBook account (free or paid depending on your needs)
3. Administrator access to both platforms

## Setting Up GitBook

### Create a New GitBook Space

1. Log in to [GitBook](https://www.gitbook.com/)
2. Click on "New Space" button
3. Choose "Create New Space"
4. Name your space "Photo Scanner Documentation"
5. Select a suitable space URL (e.g., `photo-scanner-docs`)
6. Choose "Start from scratch"
7. Click "Create Space"

### Configure GitHub Integration

1. In your GitBook space, go to "Integrations" in the sidebar
2. Select "GitHub"
3. Click "Install GitHub Integration"
4. Follow the prompts to authorize GitBook with your GitHub account
5. Select the Photo Scanner repository
6. Configure the integration:
   - **Content Directory**: `docs`
   - **Summary File**: `docs/SUMMARY.md`
   - **Branch**: `main` (or your default branch)
   - **Sync Content**: Enable two-way sync if you want to allow edits in GitBook

7. Click "Install Integration"

## Structure for GitBook

GitBook uses the SUMMARY.md file as the table of contents. Our documentation already has this file with the proper structure.

Key files for GitBook:

- **SUMMARY.md**: Defines the navigation structure
- **README.md**: Serves as the landing page
- **Image references**: Make sure image paths in Markdown are relative and accessible

## Publishing Workflow

### GitHub to GitBook (Recommended)

Our recommended workflow is:

1. Make documentation changes in the GitHub repository:
   - Create a branch for documentation changes
   - Edit the Markdown files in the `docs/` directory
   - Add or update images as needed
   - Submit a pull request for review
   - Merge approved changes to the main branch

2. GitBook will automatically:
   - Detect changes in the main branch
   - Pull the updated content
   - Rebuild and publish the documentation

### GitBook to GitHub (Alternative)

If you prefer to edit directly in GitBook:

1. Configure two-way sync in the GitHub integration settings
2. Make changes using the GitBook editor
3. GitBook will commit the changes back to the GitHub repository
4. For significant changes, consider using GitBook's "Change Request" feature, which mirrors GitHub's pull request workflow

## Custom Domain (Optional)

To use a custom domain for your documentation:

1. In your GitBook space, go to "Settings" > "Custom Domain"
2. Enter your domain (e.g., `docs.photoscanner.app`)
3. Follow the instructions to update your DNS records
4. Wait for SSL certificate provisioning (usually 24-48 hours)

## GitBook Features to Consider

Consider enabling these GitBook features:

1. **Search**: GitBook provides powerful built-in search
2. **Versioning**: For documenting different app versions
3. **Analytics**: Track which documentation pages are most viewed
4. **API Documentation**: If your app has an API
5. **Internationalization**: For supporting multiple languages

## Troubleshooting

Common issues and solutions:

### Broken Links
- Check that all internal links use relative paths
- Ensure the target files exist in the expected locations

### Missing Images
- Verify image paths are correct (should be relative to the markdown file)
- Check that images are committed to the repository

### Formatting Issues
- GitBook uses a flavor of Markdown that may interpret some syntax differently
- Test complex formatting in GitBook to ensure it renders correctly

### Sync Problems
- Check GitHub integration settings
- Verify webhook configuration
- Ensure proper permissions are set for the GitBook integration

## Maintenance

Regular maintenance tasks:

1. **Audit broken links**: Periodically check for and fix broken links
2. **Update screenshots**: When the app UI changes, update documentation images
3. **Review analytics**: Use GitBook analytics to identify areas needing improvement
4. **Update feature documentation**: Ensure documentation reflects the latest app features

## Resources

- [GitBook Documentation](https://docs.gitbook.com/)
- [GitBook GitHub Integration Guide](https://docs.gitbook.com/integrations/git-sync/github)
- [GitBook Markdown Guide](https://docs.gitbook.com/content-creation/editor/markdown)

