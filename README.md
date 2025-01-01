# AMVI

This project involves the development of a domain-specific language (DSL) in the Clojure programming language designed for data validation. The goal of this tool is to simplify the process of validating various data inputs by introducing reusable macros that provide built-in functionality for different types of validation, such as length checking, type checking, regex matching, range validation, and more. This is done by creating a suite of validation macros that work seamlessly together, allowing developers to define complex validation rules concisely and intuitively.

## Features and Code Explanation

- **String Interpolation:** The project introduces macros that allow string interpolation without needing to call the `str` function. It simplifies string formatting, making it more intuitive and readable for developers.

```bash
    (interpolation "Hello, %s, %s" "world" "world")
```

- **Custom Validation Macros:** The DSL defines several macros for creating custom validation functions with multiple rules. It handles caching to optimize performance and ensure that repeated validation checks on the same values don't cause unnecessary recalculations.

  - `def-validation`: A macro to define custom validation functions based on user-defined rules.
    ```bash
    (def-validation lr-validation [(length-validation 2 6) (regex-validation #"abc")])
    ```
  - `def-validation-inline`: Defines validation functions without the need to assign names to them, making the validation inline and concise.
    ```bash
    (let [validate (def-validation-inline [(length-validation 2 6) (regex-validation #"abc")])]
        (validate "abc"))
    ```
  - `def-validation-inline-without-cache`: A version of `def-validation-inline` without caching, used to showcase the performance difference when caching is omitted.

- **Built-in Validators:** The DSL includes common validation functions for common validation needs such as:

  - `Length Checking`: Checks if a string meets specific length constraints.
    ```bash
    (length-validation 5 10)
    ```
  - `Number Range Checking`: Ensures that a number falls within a specified range.
    ```bash
    (number-range-validation 0 100)
    ```
  - `Nil Checking`: Validates if a value is nil or not.
    ```bash
    (nil-validation)
    ```
  - `Regex Matching`: Ensures a value matches a given regular expression pattern.
    ```bash
    (regex-validation #"abc")
    ```
  - `Type Checking`: Ensures the value is of the specified type.
    ```bash
    (type-validation String)
    ```
  - `Email Validation`: Checks if an email address is valid based on a regex pattern.
    ```bash
    (email-validation)
    ```
  - `Date Validation`: Ensures that a date is before or after a given date, with strict format enforcement.
    ```bash
    (date-before-validation "2025-01-01")
    ```

- **Caching Mechanism:** The DSL uses an atom-based cache (`validation-cache`) to store results of previously computed validations. This optimizes performance, particularly when the same validation is applied multiple times on identical values.
  Part of macros for using cache:

      ```bash
      (swap! ~'validation-cache assoc-in [(str ~'value "-" ~rule-key)] ~'result)
      ```

- **Error Handling:** The macros ensure that invalid input or incorrect usage of parameters triggers informative error messages.

## Use Cases

This validation DSL is designed for use in a variety of contexts, particularly when data needs to be validated before processing it. Possible use cases include:

- **User Input Validation:** When accepting data from users, such as email addresses, phone numbers, and passwords...

- **Data Integrity Checks:** Validating data received from external sources, such as APIs or databases, before further processing or storage...

- **Configuration Validation:** Ensuring that configuration files or environmental variables follow the correct format and constraints.

## Example Usage

```bash
(def values [{:name "apple" :expiration "2025-02-14"} {:name "banana" :expiration "2025-03-11"} {:name "kiwi" :expiration "2025-10-23"}])

(let [validate-date (def-validation-inline [(date-before-validation "2026-01-01")])
      validate-length (def-validation-inline [(not-nil-validation) (length-validation 5 10)])]
    (filter #(and (validate-date (:expiration %)) (validate-length (:name %))) values))
```

## Future Improvements

Mainly idea of this project is making a DSL with strong macro system of Clojure(system extended from Lisp), considering all built-in validators are macros expanding this DSL is more than easy. All you need to do is making your own macro custom validator for concrete purpose and using that validator as parameter in macros `def-validation` or similar.

- **Enhanced Caching:** Improving the caching mechanism by introducing cache expiration or limiting cache size could further optimize performance for large-scale applications.
- **Expanded Validators:** Adding additional validators for more complex use cases (URL validation, password strength validation, etc.) could make the DSL even more flexible.
