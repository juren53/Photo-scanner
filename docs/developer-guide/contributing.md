# Contributing to Photo Scanner

This guide provides information on how to contribute to the Photo Scanner project, including code contributions, bug reports, and documentation improvements.

## Introduction

Photo Scanner is an open source project that welcomes contributions from everyone. Whether you're fixing a bug, adding a feature, improving documentation, or suggesting enhancements, your help is appreciated.

This document outlines the process for contributing to the project and provides guidelines to ensure your contributions can be efficiently reviewed and integrated.

## Getting Started

### Prerequisites

Before contributing to Photo Scanner, you should:

1. Be familiar with Android development
2. Understand Java programming
3. Know how to use Git and GitHub
4. Read the [project overview](overview.md) to understand the app's purpose and architecture

### Setting Up the Development Environment

1. **Fork the Repository**: Start by forking the [Photo Scanner repository](https://github.com/juren53/Photo-scanner) on GitHub.

2. **Clone Your Fork**:
   ```bash
   git clone https://github.com/YOUR-USERNAME/Photo-scanner.git
   cd Photo-scanner
   ```

3. **Add Upstream Remote**:
   ```bash
   git remote add upstream https://github.com/juren53/Photo-scanner.git
   ```

4. **Set Up the Development Environment**: Follow the instructions in the [build guide](build.md) to set up your development environment.

5. **Create a Branch**: Always create a new branch for your work:
   ```bash
   git checkout -b feature/your-feature-name
   # or for bug fixes
   git checkout -b fix/issue-description
   ```

## Code Style and Quality Guidelines

Photo Scanner follows the Google Java Style Guide with some project-specific additions. Adhering to these guidelines ensures code consistency and readability.

### Java Code Style

1. **Indentation**: Use 4 spaces for indentation (not tabs)
2. **Line Length**: Maximum line length of 100 characters
3. **Naming Conventions**:
   - Use `camelCase` for variable and method names
   - Use `PascalCase` for class names
   - Use `UPPER_SNAKE_CASE` for constants
4. **Braces**: Opening braces on the same line as the statement
5. **Comments**: Add Javadoc comments for all public methods and classes

### Android-Specific Guidelines

1. **Resource Naming**:
   - Layouts: `type_name.xml` (e.g., `activity_main.xml`, `dialog_metadata.xml`)
   - Drawables: `type_description.xml` (e.g., `icon_camera.xml`, `bg_button.xml`)
   - IDs: `objectType_description` (e.g., `button_capture`, `text_title`)

2. **String Resources**:
   - All user-visible strings should be in `strings.xml`
   - Use string formatting for dynamic content
   - Add comments for strings that might need context for translators

3. **Performance Considerations**:
   - Avoid unnecessary object creation
   - Use the appropriate ViewGroup for your layouts
   - Consider battery usage when implementing features

### Code Quality Requirements

1. **Unit Tests**: Write unit tests for all non-UI code
2. **Documentation**: Document public methods and complex logic
3. **Error Handling**: Properly handle exceptions and edge cases
4. **Resource Management**: Close resources in finally blocks or use try-with-resources
5. **Accessibility**: Ensure UI components are accessible

## Development Workflow

### 1. Find or Create an Issue

Before starting work, check for existing issues or create a new one to discuss your proposed changes. This helps avoid duplication of effort and ensures your contribution aligns with the project's goals.

### 2. Implement Your Changes

1. Make your code changes following the style guidelines
2. Keep commits focused and atomic
3. Write clear commit messages following this format:
   ```
   [Component]: Short summary of changes (max 50 chars)
   
   More detailed explanation if necessary. Wrap at 72 characters.
   Explain the problem being solved and how your change addresses it.
   
   Include any relevant issue numbers using "Fixes #123" or "Relates to #456".
   ```

### 3. Test Your Changes

1. Run unit tests: `./gradlew test`
2. Run instrumented tests: `./gradlew connectedAndroidTest`
3. Manually test your changes on at least one device
4. Verify that existing functionality still works as expected

### 4. Submit a Pull Request

1. **Update Your Branch**: Before submitting, update your branch with the latest changes from upstream:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Push Your Branch**:
   ```bash
   git push origin your-branch-name
   ```

3. **Create a Pull Request**:
   - Go to your fork on GitHub
   - Click "New Pull Request"
   - Select your branch and fill out the PR template
   - Provide a clear title and description
   - Link any related issues

4. **Respond to Review Feedback**:
   - Address all review comments
   - Make requested changes and push updates
   - Discuss any disagreements respectfully
   - Be patient during the review process

## Pull Request Guidelines

### PR Title and Description

- Use a clear, descriptive title
- Include the purpose of the changes
- Mention any related issues using "Fixes #123" or "Relates to #456"
- List the main changes or features added

### PR Content

- Keep PRs focused on a single issue or feature
- Break large changes into smaller, more manageable PRs
- Include screenshots or videos for UI changes
- Update documentation when necessary

### PR Review Process

1. A maintainer will review your PR
2. Automated tests must pass
3. Code style and quality guidelines must be followed
4. Changes may be requested before merging
5. Once approved, a maintainer will merge your PR

## Reporting and Fixing Bugs

### Reporting Bugs

When reporting a bug, please include:

1. **Bug Description**: Clear description of the issue
2. **Reproduction Steps**: Detailed steps to reproduce the bug
3. **Expected Behavior**: What you expected to happen
4. **Actual Behavior**: What actually happened
5. **Environment**: 
   - Device model and OS version
   - App version
   - Any relevant settings

Use the GitHub issue tracker with the "bug" label.

### Fixing Bugs

1. Comment on the issue to indicate you're working on it
2. Create a branch named `fix/brief-description`
3. Implement a fix that addresses just the reported issue
4. Add or update tests to verify the fix
5. Submit a PR following the guidelines above

## Documentation Contributions

Documentation is a critical part of the project. Contributions to improve the documentation are highly valued.

### Documentation Style Guide

1. **Language**: Use clear, concise American English
2. **Format**: Use Markdown formatting consistently
3. **Structure**: Follow the existing document structure
4. **Examples**: Include examples where appropriate
5. **Screenshots**: Add screenshots for visual clarity

### Types of Documentation Contributions

1. **User Guide Updates**:
   - Instructions for using new features
   - Clarification of existing functionality
   - Adding screenshots or diagrams

2. **Developer Guide Updates**:
   - Code examples for API usage
   - Architecture explanations
   - Build or setup instructions

3. **Code Documentation**:
   - Javadoc comments for classes and methods
   - Inline comments for complex logic
   - README updates

### Documentation Updates Process

1. For simple changes, edit the file directly on GitHub
2. For substantial changes:
   - Create a branch
   - Make your changes
   - Submit a PR
3. Preview your changes before submitting

## Testing Requirements

Testing is essential to maintaining a stable application. All contributions should include appropriate tests.

### Types of Tests

1. **Unit Tests**: Test individual components in isolation
   - Location: `app/src/test/java/`
   - Run with: `./gradlew test`

2. **Instrumented Tests**: Test components that require Android framework
   - Location: `app/src/androidTest/java/`
   - Run with: `./gradlew connectedAndroidTest`

3. **UI Tests**: Test user interactions and UI flows
   - Use Espresso for UI testing
   - Focus on critical user journeys

### Testing Guidelines

1. **Coverage**: Aim for high test coverage of new code
2. **Isolation**: Tests should be independent of each other
3. **Readability**: Test names should describe what they're testing
4. **Maintainability**: Keep tests simple and focused
5. **Edge Cases**: Test boundary conditions and error scenarios

### Required Tests for Contributions

| Contribution Type | Required Tests |
|-------------------|----------------|
| Bug fix | Test that verifies the bug is fixed |
| New feature | Unit tests for logic, UI tests for user interaction |
| Performance improvement | Benchmark tests showing improvement |
| Refactoring | Tests ensuring behavior remains unchanged |

## Getting Help

If you need assistance with your contribution, there are several ways to get help:

1. **GitHub Issues**: For questions related to specific issues
2. **Discussions**: For general questions about the project
3. **Documentation**: Review the existing documentation first

### Asking Good Questions

When seeking help:

1. Be specific about what you're trying to accomplish
2. Describe what you've already tried
3. Include relevant code snippets or screenshots
4. Mention your development environment details

### Community Guidelines

1. Be respectful and considerate
2. Help others when you can
3. Stay on topic in discussions
4. Follow the project's code of conduct

## Recognition

Contributors are recognized in several ways:

1. Your name in the commit history
2. Mention in release notes for significant contributions
3. Addition to the contributors list for regular contributors

## License

By contributing to Photo Scanner, you agree that your contributions will be licensed under the same license as the project (specified in the repository's LICENSE file).

## Related Documentation

- [Project Overview](overview.md)
- [Architecture](architecture.md)
- [Build Instructions](build.md)

