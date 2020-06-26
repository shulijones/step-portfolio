// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
Sets the header for every page.
 */
function getHeader() {
    document.getElementById("header").innerHTML += `<p class="header-text"> Shuli Jones </p>
<div id="navbar">
    <a href="index.html" class="navlink">Home</a> 
    <div class="dropdown">
        <a href="projects.html" class="navlink">Projects</a>
        <div class="dropdown-list">
            <a href="projects.html#tm" onclick="showMore('tm')">Task Manager</a>
            <a href="projects.html#ma" onclick="showMore('ma')">Matching Algorithm </a>
            <a href="projects.html#l" onclick="showMore('l')">Loft Bed</a>
        </div>
    </div> 
    <div class="dropdown">
        <a href="elsewhere.html" class="navlink">Elsewhere</a>
        <div class="dropdown-list">
            <a href="https://github.com/shulijones">GitHub</a>
            <a href="https://mitadmissions.org/blogs/author/jonsh/">MIT Admissions</a>
        </div>
    </div>
</div>`;
}

/**
 * Shows a section of hidden text (e.g. after a user clicks to indicate they want more information).
 * If the user clicks again, toggles to hide the text.
 * This only works when already on the page with the hidden text, not from another page.
 */
function showMore(text) {
  console.log('starting showMore');
  var element = document.getElementById(`more-info--${text}`);
  
  if (element.style.maxHeight === '0px' || element.style.maxHeight === '') {
    element.style.maxHeight = '500px'; 
    /* Magic number to get around the fact that you can't animate to auto */
  }
  else {
    element.style.maxHeight = '0px';
  }
}

/**
 * Checks if the user is at an anchor for a particular section of hidden text,
 * and reveals it if they are.
 */
function checkShowMore() {
  if (window.location.href.includes("#")) {
    showMore(window.location.href.split("#").pop());
  }
}

/**
 * Adds a random fact to the page. Deprecated.
 */
function addRandomFact() {
  const facts =
      ['No one knows exactly how many languages there are, \
      but the number is estimated to be between 6 and 8 thousand.',
       'Tetris is NP-complete.', 
       'The youngest person ever to swim across the English channel \
       was a 17-year-old Canadian named Marilyn Bell.', 
       'The first video ever played on MTV was the music video \
       for the song "Video Killed the Radio Star", by the Buggles.'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}
