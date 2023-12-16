package main

import (
    "bufio"
    "fmt"
    "os"
    "regexp"
    "strconv"
)

func main() {
    re := regexp.MustCompile(`.*?(\d).*?(\d?)\D*$`)

    file, _ := os.Open("../resources/input-01.txt")
    defer file.Close()

    total := 0
    scanner := bufio.NewScanner(file)
    for scanner.Scan() {
        matches := re.FindStringSubmatch(scanner.Text())
        d1 := matches[1]
        d2 := matches[2]
        if d2 == "" {
            d2 = d1
        }
        n, _ := strconv.Atoi(d1 + d2)
        total += n
    }
    fmt.Println(total)
}
