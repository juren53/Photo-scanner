
*Last update: April 21, 2025*


Thank you for your interest in improving the Photo Scanner documentation! This guide will help you understand how to contribute to our documentation effectively.

## Why Documentation Matters

Good documentation is essential for any software project. It helps users understand how to use the application, assists developers in maintaining and extending the code, and builds community around the project. Your contributions to the documentation directly improve the experience of Photo Scanner users.

## Types of Documentation Contributions

We welcome various types of documentation improvements:

- **Bug fixes**: Correcting errors, typos, or outdated information
- **Clarifications**: Making existing documentation clearer or more detailed
- **New content**: Adding documentation for new features or undocumented aspects
- **Organization**: Improving structure, navigation, or findability
- **Visual aids**: Adding helpful screenshots, diagrams, or illustrations
- **Translations**: Translating documentation into other languages (if supported)

## Setting Up Your Environment

### Basic Setup (for Simple Changes)

For small changes like fixing typos or clarifying text, you can edit files directly on GitHub:

1. Navigate to the file you want to edit on GitHub
2. Click the pencil icon (Edit this file)
3. Make your changes in the editor
4. Preview changes using the "Preview" tab
5. Add a commit message describing your changes
6. Choose "Create a new branch and start a pull request"

### Advanced Setup (for Substantial Changes)

For more significant changes, you'll want to work with the documentation locally:

1. **Fork the repository**:
   - Visit [https://github.com/juren53/Photo-scanner](https://github.com/juren53/Photo-scanner)
   - Click the "Fork" button to create your own copy

2. **Clone your fork**:
   ```bash
   git clone https://github.com/YOUR-USERNAME/Photo-scanner.git
   cd Photo-scanner
   ```

3. **Create a branch for your changes**:
   ```bash
   git checkout -b docs/your-feature-name
   ```

4. **Install a Markdown editor** (recommended):
   - [Visual Studio Code](https://code.visualstudio.com/) with Markdown extensions
   - [Typora](https://typora.io/)
   - [MarkText](https://marktext.app/)

5. **Make your changes**:
   - Edit the Markdown files in the `docs` directory
   - Preview changes using your Markdown editor

6. **Commit your changes**:
   ```bash
   git add docs/changed-file.md
   git commit -m "docs: Add information about feature X"
   ```

7. **Push your changes**:
   ```bash
   git push origin docs/your-feature-name
   ```

8. **Create a pull request**:
   - Go to your fork on GitHub
   - Click "Compare & pull request"
   - Provide a clear description of your changes

## Documentation Structure

The Photo Scanner documentation is organized into two main sections:

### User Guide

Located in `docs/user-guide/`, this section is intended for end-users of the application:

- `introduction.md`: Overview of the application
- `getting-started.md`: Installation and initial setup
- `basic-features.md`: Core functionality
- `advanced-features.md`: More sophisticated features
- Feature-specific guides (e.g., `file-naming.md`, `metadata.md`)
- `troubleshooting.md`: Common issues and solutions

### Developer Guide

Located in `docs/developer-guide/`, this section is for developers who want to contribute to or extend the application:

- `overview.md`: High-level project overview
- `architecture.md`: System design and components
- `build.md`: Build instructions
- `contributing.md`: Development contribution guidelines

### Special Files

- `README.md`: The landing page for the documentation
- `SUMMARY.md`: The table of contents, referenced by the app's help system
- `CONTRIBUTING.md`: This guide for documentation contributors

## Documentation Style Guidelines

### Writing Style

1. **Be clear and concise**:
   - Use simple, direct language
   - Avoid jargon unless it's defined
   - Use active voice when possible

2. **Be conversational but professional**:
   - Write as if you're explaining to a colleague
   - Avoid overly formal or academic language
   - Don't use slang or too-casual expressions

3. **Be inclusive**:
   - Use gender-neutral language
   - Avoid assumptions about technical knowledge
   - Consider users with different abilities

### Markdown Formatting

We use GitHub-flavored Markdown for our documentation:

1. **Headers**:
   - Use `#` for page titles
   - Use `##` for major sections
   - Use `###` for subsections
   - Use `####` for minor subsections

2. **Lists**:
   - Use `-` for unordered lists
   - Use `1.` for ordered lists
   - Indent with four spaces for nested lists

3. **Code and Commands**:
   - Use backticks (\`) for inline code
   - Use triple backticks (\`\`\`) for code blocks
   - Specify the language for syntax highlighting

4. **Emphasis**:
   - Use `*italics*` for subtle emphasis
   - Use `**bold**` for strong emphasis

5. **Links**:
   - Use `[text](URL)` for links
   - For internal links, use relative paths

### File Organization

- Each document should focus on a single topic
- Use descriptive filenames with lowercase and hyphens (e.g., `file-naming.md`)
- Place images in the `docs/images/` directory
- Reference images using relative paths

## Submitting Documentation Updates

### For Minor Changes

1. Edit the file directly on GitHub
2. Provide a clear commit message
3. Submit a pull request
4. Respond to any feedback

### For Substantial Changes

1. Create an issue first to discuss the proposed changes
2. Fork the repository and create a branch
3. Make your changes locally
4. Test your changes by previewing the Markdown
5. Commit and push your changes
6. Create a pull request with a detailed description
7. Respond to feedback in the review process

### Pull Request Guidelines

- Use a clear, descriptive title
- Explain what you've changed and why
- Link to any related issues
- Include screenshots of before/after for UI documentation changes
- Be open to suggestions and improvements

## Documentation Versioning

The documentation should align with the current release of the application:

### Version Tagging

- Documentation in the `main` branch should reflect the latest stable release
- Major version updates may require documentation to be tagged with version numbers
- Use conditional text or notes for version-specific features (e.g., "Available in v1.3+")

### Version-Specific Content

When a feature changes significantly between versions:

1. Note the version when the feature was introduced or changed
2. If applicable, describe how the feature worked in previous versions
3. Use screenshots appropriate to the current version

## Adding Images and Screenshots

Images and diagrams enhance documentation significantly. Here are guidelines for adding visual elements:

### Screenshots

1. **Capture high-quality screenshots**:
   - Use a device or emulator with standard density
   - Capture the relevant portion of the screen
   - Ensure no sensitive information is visible

2. **Process screenshots**:
   - Resize large screenshots to a reasonable size (max width: 800px)
   - Optimize file size using tools like [TinyPNG](https://tinypng.com/)
   - Use PNG format for screenshots

3. **Name files descriptively**:
   - Use lowercase with hyphens
   - Include the feature name
   - Example: `file-naming-dialog.png`

### Diagrams

1. **Create clear diagrams**:
   - Use simple shapes and readable text
   - Maintain a consistent style
   - Label important elements

2. **File formats**:
   - Use SVG when possible for diagrams (for scalability)
   - Otherwise, use PNG with transparent backgrounds

3. **Source files**:
   - If possible, include source files for diagrams
   - Store in `docs/images/source/`

### Image References

When adding images to documentation:
```markdown
![Camera Interface](../images/camera-interface.png)
*Figure 1: The camera interface with important controls labeled*
```
Add a caption when helpful:

```markdown
![Resolution Settings Interface](../images/resolution-settings.png)
*Figure 1: The resolution settings interface with options labeled*
```

## Review Process

All documentation contributions are reviewed to ensure quality and consistency:

1. **Initial review**: Basic checks for format, style, and accuracy
2. **Technical review**: Verification that technical details are correct
3. **Editorial review**: Assessment of clarity, organization, and completeness
4. **Final approval**: Acceptance and merging of the contribution

## Getting Help

If you have questions about contributing to documentation:

- Check existing documentation first
- Look for similar examples in the current docs
- Create an issue labeled "documentation question"
- Reach out on the project's discussion forum

## Recognition

Documentation contributors are valued members of the Photo Scanner community:

- All contributors are acknowledged in the repository
- Significant documentation contributions are mentioned in release notes
- Documentation improvements directly help thousands of users

Thank you for helping improve the Photo Scanner documentation!

