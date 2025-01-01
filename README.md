# AMVI-Clojure

This project involves the development of a domain-specific language (DSL) in the Clojure programming language designed for data validation. The goal of this tool is to simplify the process of validating various data inputs by introducing reusable macros that provide built-in functionality for different types of validation, such as length checking, type checking, regex matching, range validation, and more. This is done by creating a suite of validation macros that work seamlessly together, allowing developers to define complex validation rules concisely and intuitively.

## Features

- **String Interpolation:** The project introduces macros that allow string interpolation without needing to call the `str` function. It simplifies string formatting, making it more intuitive and readable for developers.

- **Custom Validation Macros:** The DSL defines several macros for creating custom validation functions with multiple rules. It handles caching to optimize performance and ensure that repeated validation checks on the same values don't cause unnecessary recalculations.
  `def-validation`: A macro to define custom validation functions based on user-defined rules.
  `def-validation-inline`: Defines validation functions without the need to assign names to them, making the validation inline and concise.
  `def-validation-inline-without-cache`: A version of `def-validation-inline` without caching, used to showcase the performance difference when caching is omitted.

- **Built-in Validators:** The DSL includes common validation functions for common validation needs such as:
  `Length Checking`: Checks if a string meets specific length constraints.
  `Number Range Checking`: Ensures that a number falls within a specified range.
  `Nil Checking`: Validates if a value is nil or not.
  `Regex Matching`: Ensures a value matches a given regular expression pattern.
  `Type Checking`: Ensures the value is of the specified type.
  `Email Validation`: Checks if an email address is valid based on a regex pattern.
  `Date Validation`: Ensures that a date is before or after a given date, with strict format enforcement.

- **Caching Mechanism:** The DSL uses an atom-based cache (`validation-cache`) to store results of previously computed validations. This optimizes performance, particularly when the same validation is applied multiple times on identical values.

- **Error Handling:** The macros ensure that invalid input or incorrect usage of parameters triggers informative error messages.

## Use Cases

This validation DSL is designed for use in a variety of contexts, particularly when data needs to be validated before processing it. Possible use cases include:

- **User Input Validation:** When accepting data from users, such as email addresses, phone numbers, and passwords...

- **Data Integrity Checks:** Validating data received from external sources, such as APIs or databases, before further processing or storage...

- **Configuration Validation:** Ensuring that configuration files or environmental variables follow the correct format and constraints.
