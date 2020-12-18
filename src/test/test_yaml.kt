import milton.kotlin.yaml.ParseYaml

fun main() {
    val myyaml = """
        some_key: some_val
        some_dict:
            another: value
            some_other_dict:
                another_val: test
                and_list:
                    - num1
                    - num2
                    - num3
        test_key:
            - listval1
            - listval2
            - listval3
    """.trimIndent()

    val obj = ParseYaml(myyaml)

    println(obj["some_key"])
    println(obj["some_dict"]["another"])
    println(obj["some_dict"]["some_other_dict"]["and_list"].asList())
    println(obj["test_key"][1])
}