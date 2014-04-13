// Poll for text/vnd.graphviz scripts containing DOT files and replace
// their parents with rendered SVG versions
window.setInterval(function() {
    $("script[type='text/vnd.graphviz']").each(function(i, e) {
        var $e = $(e);
        var dot = Viz($e.html(), "svg");
        // Strip comments, xml and doctype declarations from the output
        dot = dot.trim().split("\n").slice(6).join("\n");
        $e.parent().html(dot);
    });
}, 1000);
