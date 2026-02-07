import { BaseStyles, ThemeProvider, PageLayout, Heading, Text } from "@primer/react";

export default function App() {
  return (
    <ThemeProvider>
      <BaseStyles>
        <PageLayout>
          <PageLayout.Content>
            <Heading>Scala 3 Benchmarks</Heading>
            <Text>Hello, world!</Text>
          </PageLayout.Content>
        </PageLayout>
      </BaseStyles>
    </ThemeProvider>
  );
}
