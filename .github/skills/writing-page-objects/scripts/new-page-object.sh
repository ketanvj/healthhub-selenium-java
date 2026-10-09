#!/usr/bin/env bash
# Prints a Page Object skeleton for the HealthHub Selenium framework.
# Usage: ./new-page-object.sh FeatureName
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <FeatureName>" >&2
  exit 1
fi

NAME="$1"

cat <<JAVA
package com.healthhub.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ${NAME}Page extends BasePage {

    // Replace with the real data-testid values, found by navigating to the
    // live page with Playwright MCP (or DevTools) and inspecting the DOM —
    // do not guess them, and do not read application source.

    public ${NAME}Page(WebDriver driver) {
        super(driver);
    }
}
JAVA
