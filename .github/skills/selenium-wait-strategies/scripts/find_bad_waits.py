#!/usr/bin/env python3
"""
find_bad_waits.py — Scans Selenium Java test source files for brittle wait patterns
that should be replaced with explicit WebDriverWait / ExpectedConditions.

Usage:
    python scripts/find_bad_waits.py --dir src/test/java/
    python scripts/find_bad_waits.py --file src/test/java/tests/LoginTest.java

Output:
    Prints a report of every bad wait found with file, line number, the offending
    code, and the recommended replacement.

Requirements:
    Python 3.8+ (stdlib only)
"""

import argparse
import os
import re
import sys
from dataclasses import dataclass
from typing import List


@dataclass
class Finding:
    file: str
    line_number: int
    code: str
    pattern_name: str
    recommendation: str


# Patterns to detect, each with name and recommended replacement
BAD_WAIT_PATTERNS = [
    {
        "name": "Thread.sleep()",
        "regex": re.compile(r'Thread\.sleep\s*\(\s*\d+\s*\)', re.IGNORECASE),
        "recommendation": (
            "Replace with WebDriverWait + ExpectedConditions.\n"
            "  Example: wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(\"element\")));"
        ),
    },
    {
        "name": "implicitlyWait()",
        "regex": re.compile(r'\.implicitlyWait\s*\(', re.IGNORECASE),
        "recommendation": (
            "Remove implicit waits — they conflict with WebDriverWait and cause doubled timeouts.\n"
            "  Use WebDriverWait with ExpectedConditions throughout instead."
        ),
    },
    {
        "name": "driver.manage().timeouts().implicitlyWait()",
        "regex": re.compile(r'manage\s*\(\s*\)\s*\.timeouts\s*\(\s*\)\s*\.implicitlyWait\s*\(', re.IGNORECASE),
        "recommendation": (
            "Remove implicit wait — use only explicit WebDriverWait.\n"
            "  Mixing implicit and explicit waits leads to unpredictable behaviour."
        ),
    },
    {
        "name": "sleep() without Thread prefix (import static)",
        "regex": re.compile(r'(?<!\w)sleep\s*\(\s*\d+\s*\)', re.IGNORECASE),
        "recommendation": (
            "Likely Thread.sleep via static import. Replace with WebDriverWait + ExpectedConditions."
        ),
    },
    {
        "name": "TimeUnit.SECONDS.sleep()",
        "regex": re.compile(r'TimeUnit\.\w+\.sleep\s*\(', re.IGNORECASE),
        "recommendation": (
            "Replace TimeUnit sleep with WebDriverWait + ExpectedConditions."
        ),
    },
    {
        "name": "driver.wait() (legacy)",
        "regex": re.compile(r'driver\.wait\s*\(', re.IGNORECASE),
        "recommendation": (
            "driver.wait() is a legacy pattern. Use new WebDriverWait(driver, Duration.ofSeconds(10)) instead."
        ),
    },
    {
        "name": "Hardcoded pageLoadTimeout with findElement (no explicit wait)",
        "regex": re.compile(r'pageLoadTimeout\s*\(\s*\d+\s*,\s*TimeUnit\.', re.IGNORECASE),
        "recommendation": (
            "pageLoadTimeout alone is not a substitute for element-level explicit waits.\n"
            "  Add WebDriverWait + ExpectedConditions for element interactions."
        ),
    },
]


def scan_file(filepath: str) -> List[Finding]:
    """Scan a single Java file for bad wait patterns."""
    findings: List[Finding] = []
    try:
        with open(filepath, "r", encoding="utf-8", errors="replace") as f:
            lines = f.readlines()
    except OSError as e:
        print(f"WARNING: Cannot read {filepath}: {e}", file=sys.stderr)
        return findings

    for line_num, line in enumerate(lines, start=1):
        stripped = line.strip()
        # Skip comments
        if stripped.startswith("//") or stripped.startswith("*"):
            continue
        for pattern in BAD_WAIT_PATTERNS:
            if pattern["regex"].search(line):
                findings.append(Finding(
                    file=filepath,
                    line_number=line_num,
                    code=stripped[:120],
                    pattern_name=pattern["name"],
                    recommendation=pattern["recommendation"],
                ))
    return findings


def collect_java_files(directory: str) -> List[str]:
    """Recursively collect all .java files under a directory."""
    java_files = []
    for root, _, files in os.walk(directory):
        for f in files:
            if f.endswith(".java"):
                java_files.append(os.path.join(root, f))
    return sorted(java_files)


def print_report(findings: List[Finding], files_scanned: int) -> None:
    """Print structured report of all findings."""
    print("=" * 70)
    print("SELENIUM WAIT AUDIT REPORT")
    print("=" * 70)
    print(f"Files scanned : {files_scanned}")
    print(f"Issues found  : {len(findings)}")
    print()

    if not findings:
        print("✅ No brittle wait patterns found.")
        return

    # Group by file
    by_file: dict = {}
    for f in findings:
        by_file.setdefault(f.file, []).append(f)

    for filepath, file_findings in sorted(by_file.items()):
        print(f"📄 {filepath}  ({len(file_findings)} issue(s))")
        print("-" * 60)
        for finding in file_findings:
            print(f"  Line {finding.line_number:4d}  [{finding.pattern_name}]")
            print(f"  Code: {finding.code}")
            print(f"  Fix:  {finding.recommendation}")
            print()

    print("=" * 70)
    print("SUMMARY")
    print("-" * 70)

    # Count by pattern
    pattern_counts: dict = {}
    for f in findings:
        pattern_counts[f.pattern_name] = pattern_counts.get(f.pattern_name, 0) + 1

    for pattern, count in sorted(pattern_counts.items(), key=lambda x: -x[1]):
        print(f"  {count:3d}x  {pattern}")

    print()
    print("Next steps:")
    print("  1. Fix Thread.sleep() calls first — they cause the most flakiness.")
    print("  2. Remove all implicitlyWait() calls — they conflict with WebDriverWait.")
    print("  3. Use the selenium-wait-strategies skill to apply the correct pattern.")
    print("=" * 70)


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Scan Selenium Java tests for brittle wait patterns."
    )
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--dir",  help="Directory to scan recursively for .java files")
    group.add_argument("--file", help="Single .java file to scan")
    args = parser.parse_args()

    if args.file:
        if not os.path.isfile(args.file):
            print(f"ERROR: File not found: {args.file}", file=sys.stderr)
            sys.exit(1)
        files = [args.file]
    else:
        if not os.path.isdir(args.dir):
            print(f"ERROR: Directory not found: {args.dir}", file=sys.stderr)
            sys.exit(1)
        files = collect_java_files(args.dir)
        if not files:
            print(f"No .java files found in {args.dir}")
            sys.exit(0)

    all_findings: List[Finding] = []
    for f in files:
        all_findings.extend(scan_file(f))

    print_report(all_findings, len(files))

    # Exit with non-zero code if issues found (useful for CI gates)
    sys.exit(1 if all_findings else 0)


if __name__ == "__main__":
    main()
