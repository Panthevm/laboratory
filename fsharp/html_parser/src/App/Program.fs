open System
open System.Text
open FSharp.Data
open System.Collections.Generic
open System.Text.RegularExpressions

let extract_content (node : FSharp.Data.HtmlNode option) =
    match node with
    | Some node -> FSharp.Data.HtmlNode.directInnerText(node)
    | None      -> None.ToString()

let extract_page_collection (node : FSharp.Data.HtmlDocument) =
    node.CssSelect("div.paginator__count")
    |> List.head
    |> FSharp.Data.HtmlNode.directInnerText
    |> fun content -> content.Split ' '
    |> Array.last
    |> int
    |> fun page_number -> seq {0 .. page_number}

let extract_catalog_element(node:FSharp.Data.HtmlNode) =
    let element = Dictionary<string,string> ()
    element.Add("Name",
                node.CssSelect(".indexGoods__item__name")
                |> List.tryHead
                |> extract_content)
    element.Add("Price",
                node.CssSelect("[itemprop='price']")
                |> List.tryHead
                |> extract_content)
    element.Add("Available",
                node.CssSelect(".catalog__displayedItem__availabilityCount")
                |> List.tryHead
                |> extract_content)
    element

let extract_catalog_elements(url:string, page:int) =
    printf "Page: %A\n" page
    |> string
    |> fun page_string -> url + "?browse_mode=4?page=" + page_string
    |> FSharp.Data.HtmlDocument.Load
    |> fun node -> node.CssSelect("[itemtype='http://schema.org/Product']")
    |> List.map extract_catalog_element

let extract_catalog(url:string) =
    url
    |> FSharp.Data.HtmlDocument.Load
    |> extract_page_collection
    |> Seq.take 10 // LIMIT
    |> Seq.map (fun page -> extract_catalog_elements(url,page))
    |> Seq.concat

let create_csv(path,elements) =
    elements
    |> Seq.map (fun (element:Dictionary<string,string>) ->
                element.Item("Name")  + "," +
                element.Item("Price") + "," +
                element.Item("Available"))
    |> fun rows -> System.IO.File.WriteAllLines(path, rows)

[<EntryPoint>]
let main input =
    let arguments = input |> Seq.pairwise |> dict
    let catalog   = arguments.Item("--url") |> extract_catalog
    let csv       = create_csv(arguments.Item ("--csv"), catalog)
    0
