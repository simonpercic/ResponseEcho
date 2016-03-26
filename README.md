# ResponseEcho

Java Spring web app that gets a Base64 encoded and gzipped string param, decodes and unpacks it and then echoes it back as a plain string.

## Purpose

Used as a complementary web app for [OkLog](https://github.com/simonpercic/OkLog).

## Run

```
cd ResponseEcho
./gradlew stage bootRun
```

## Error handling

Since ResponseEcho is a development tool designed to be used in conjunction with [OkLog](https://github.com/simonpercic/OkLog), there is no error handling. On (any) error, the app will do a HTTP redirect back to this page.

## License

Open source, distributed under the MIT License. See [LICENSE](LICENSE) for details.
