# Contributing to Pismo Account Service

Thank you for your interest in contributing to the Pismo Account Service!

## Development Setup

### Prerequisites
- Java 21 or higher
- Maven 3.6+ (or use the included wrapper)
- Docker (optional, for PostgreSQL)
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd pismo
   ```

2. **Build the project**
   ```bash
   mvnw clean install
   ```

3. **Run tests**
   ```bash
   mvnw test
   ```

4. **Start the application**
   ```bash
   mvnw spring-boot:run
   ```

## Code Style Guidelines

### Java Conventions
- Use Java 21 features where appropriate
- Follow standard Java naming conventions
- Keep methods focused and concise (Single Responsibility Principle)
- Use meaningful variable and method names
- Write self-documenting code with appropriate comments

### Code Organization
```
src/main/java/com/pismo/account/
├── controller/     # REST endpoints
├── service/        # Business logic
├── repository/     # Data access
├── domain/         # Entities and enums
├── dto/            # Data transfer objects
└── exception/      # Custom exceptions
```

### Annotations
- Use `@RestController` for REST controllers
- Use `@Service` for service layer
- Use `@Repository` for repositories
- Use `@Transactional` for transactional methods
- Use Lombok annotations to reduce boilerplate

## Testing Guidelines

### Unit Tests
- Write unit tests for all service methods
- Use Mockito for mocking dependencies
- Aim for high code coverage (>80%)
- Test both success and error scenarios

Example:
```java
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository repository;
    
    @InjectMocks
    private AccountService service;
    
    @Test
    void shouldCreateAccount() {
        // Arrange, Act, Assert
    }
}
```

### Integration Tests
- Test complete request/response flows
- Use `@SpringBootTest` for integration tests
- Test API endpoints with RestAssured
- Verify database state changes

### Test Naming
- Use descriptive test method names
- Format: `should[Expected]When[Condition]`
- Example: `shouldReturnAccountWhenValidIdProvided`

## Git Workflow

### Branch Naming
- `feature/` - New features
- `bugfix/` - Bug fixes
- `hotfix/` - Critical fixes
- `refactor/` - Code refactoring

Example: `feature/add-transaction-history`

### Commit Messages
Follow the conventional commits format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `test`: Tests
- `refactor`: Code refactoring
- `style`: Code style changes
- `chore`: Build/tools changes

Example:
```
feat(transactions): add transaction history endpoint

Implemented GET /transactions endpoint to retrieve
transaction history for an account.

Closes #123
```

## Pull Request Process

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write clean, tested code
   - Follow code style guidelines
   - Update documentation if needed

3. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: your feature description"
   ```

4. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request**
   - Provide a clear description
   - Reference related issues
   - Ensure all tests pass
   - Request review from maintainers

### PR Checklist
- [ ] Code follows project style guidelines
- [ ] All tests pass
- [ ] New tests added for new features
- [ ] Documentation updated
- [ ] No unnecessary console logs or comments
- [ ] Commit messages are clear and descriptive

## API Design Guidelines

### REST Principles
- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Return proper HTTP status codes
- Use JSON for request/response bodies
- Follow RESTful URL patterns

### Status Codes
- `200 OK` - Successful GET
- `201 Created` - Successful POST
- `400 Bad Request` - Validation error
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate resource
- `500 Internal Server Error` - Server error

### Error Responses
Always return structured error responses:
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/resource/123"
}
```

## Database Guidelines

### Entity Design
- Use proper JPA annotations
- Define relationships clearly
- Add appropriate indexes
- Use database constraints

### Migrations
- Never modify existing migration scripts
- Test migrations on development database first
- Document schema changes

## Documentation

### Code Documentation
- Document public APIs with Javadoc
- Add inline comments for complex logic
- Keep documentation up-to-date

### API Documentation
- Update Swagger/OpenAPI annotations
- Provide request/response examples
- Document error scenarios

### README Updates
- Keep installation instructions current
- Update feature list
- Add troubleshooting sections

## Questions or Issues?

If you have questions or run into issues:
1. Check existing issues
2. Review documentation
3. Ask in discussions
4. Create a new issue

Thank you for contributing! 🎉
